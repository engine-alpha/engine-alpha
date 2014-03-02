/*
 * Engine Alpha ist eine anfaengerorientierte 2D-Gaming Engine.
 *
 * Copyright (C) 2011  Michael Andonie
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package ea.internal.phy;

import ea.BoundingRechteck;
import ea.FallDummy;
import ea.FallReagierbar;
import ea.Raum;
import ea.StehReagierbar;
import ea.Vektor;
import ea.internal.util.Logger;

/**
 * Ein Gravitator sorgt fuer das Verhalten eines Aktiv-Raum-Objektes in der Physik.
 * 
 * @author Michael Andonie
 */
public class Gravitator
extends PhysikClient {

    /**
     * Eine imaginaere Pseudo-Beschleunigung fuer die Schwerkraft.
     */
    private int schwerkraft = 4;

    /**
     * Der Trend der aktuellen Y-Verschiebung
     */
    private int yTrend = 0;

    /**
     * Der Zaehöer des Y-Trends.
     */
    private int zaehler = 1;

    /**
     * Gibt an, ob das Objekt in der naechsten TICK-Runde springen soll.
     */
    private boolean sprungStart = false;

    /**
     * Ob das Ziel-Objekt im letzten Schritt "gefallen" oder gesprungen ist.
     */
    private boolean zuletztGefallen = false;

    /**
     * Die Physik
     */
    private final Physik physik = Physik.getPhysik();

    /**
     * Ob das Ziel-Raum-Objekt von Schwerkraft beeinflusst wird
     */
    private boolean hatSchwerkraft = true;

    /**
     * Das FallReagierbar-Interface, das auf zu tiefes Fallen reagieren soll.
     */
    private FallReagierbar fListener = FallDummy.getDummy();

    /**
     * Das StehReagierbar-Interface, das auf stehen reagieren soll.
     */
    private StehReagierbar sListener = StehDummy.getDummy();

    /**
     * Die kritische Tiefe, ab der der Listener regelmaessig informiert wird.
     */
    private int kritischeTiefe;

    /**
     * Konstruktor.
     * @param ziel  Das Ziel des Gravitators: Dieses Objekt wird von ihm ueberwacht.
     * @param   p   Die Aktive Physik
     */
    public Gravitator(Raum ziel) {
        super(ziel);
        physik.aktivAnmelden(this);
    }

    /**
     * Methode zur Weitergabe eines Rechenschrittes
     * @param runde Die Runde (1 - 10)
     */
    public void tick(int runde) {
        ziel.verschieben(physik.entblocken(ziel.dimension()));
        if(ziel.dimension().y > kritischeTiefe) {
            fListener.fallReagieren();
        }
        if(!(runde == 1)) {
            return;
        }
        if(!hatSchwerkraft) {
            return;
        }
        boolean steht = steht();
        if(!steht) {
            //FALL-ZAEHLEN
            if(zaehler % schwerkraft == 0) {
                if(yTrend < 10) {
                    zaehler = 1;
                    yTrend++;
                }
            } else {
                zaehler++;
            }
        } else if(!sprungStart) {
            if(zuletztGefallen) {
                sListener.stehReagieren();
            }
            yTrend = 0;
            zuletztGefallen = false;
            zaehler = 1;
        } else {
            sprungStart = false;
            zuletztGefallen = false;
        }
        bewegen(new Vektor(0, yTrend));
    }

    /**
     * Bewegt das Raum-Objekt mithilfe des Gravitators.
     * @param v Die Bewegung beschrieben durch einen Vektor.
     * @return <code>true</code>, sollte die Bewegung vollfuehrt worden sein, ohne, dass man passiv geblockt wurde. Wurde
     * man in der vollen Ausfuehrung der Bewegung gehindert, ist die Rueckgabe <code>false</code>.
     */
    @Override
    public boolean bewegen(Vektor v) {
        ziel.verschieben(physik.entblocken(ziel.dimension()));
        return (xVersch(v.x) &  yVersch(v.y));
    }

    /**
     * Vollfuehrt portionsweises (in Pixelschritten) Verschieben auf der X-Richtung.
     * @param dX    Die X-Aenderung (Delta-X)
     * @return <code>true</code>, wenn die Bewegung in X-Richtung ohne Passiv-Block moeglich war, sonst <code>false</code>.
     */
    public boolean xVersch(float dX) {
        float z;
        if(dX > 0) {
            z = 1;
        } else if(dX < 0) {
            z = -1;
            dX = -dX;
        } else {
            return true;
        }
        Vektor v = new Vektor(z, 0);
        for(int i = 0; i < dX; i++) {
            BoundingRechteck test = ziel.dimension().verschobeneInstanz(v);
            if(physik.inPassivem(test)) {
                return false;
            }
            ziel.verschieben(v);
        }
        return true;
    }

    /**
     * Vollfuehrt portionsweises (in Pixelschritten) Verschieben auf der Y-Richtung unter beruecksichtung von
     * SChwerkrafteigenschaften.
     * @param dX    Die Y-Aenderung (Delta-Y)
     * @return <code>true</code>, wenn die Bewegung in Y-Richtung ohne Passiv-Block moeglich war, sonst <code>false</code>.
     */
    public boolean yVersch(float dY) {
    	float z;
        if(dY > 0) {
            z = 1;
        } else if(dY < 0) {
            z = -1;
            dY = -dY;
        } else {
            return true;
        }
        Vektor v = new Vektor(0, z);
        for(int i = 0; i < dY; i++) {
            BoundingRechteck test = ziel.dimension().verschobeneInstanz(v);
            if(physik.inPassivem(test)) {
                yTrend = 0;
                if(z > 0) {
                    sListener.stehReagieren();
                }
                zuletztGefallen = false;
                return false;
            }
            zuletztGefallen = true;
            ziel.verschieben(v);
        }
        return true;
    }

    /**
     * Diese Methode wird immer dann aufgerufen, wenn ein Client nicht weiter benoetigt
     * wird, und er alle seine Funktionen beenden soll, um die von ihm belegten Ressourcen
     * freizugeben.
     */
    @Override
    public void aufloesen() {
        physik.aktivAbmelden(this);
    }

    /**
     * Laesst das anliegende Raum-Objekt springen. Dies ist nur dann möglich, wenn das
     * anliegende Raum-Objekt "steht". Also, wenn es auf einemm Passiv-Objekt steht.
     * @param kraft Die Sprungkraft.
     */
    @Override
    public boolean sprung(int kraft) {
        if(!hatSchwerkraft) {
            System.err.println("Achtung! Ein Raum-Objekt, fuer das KEINE Schwerkraft gilt, kann nicht springen!");
            return false;
        }
        if(steht()) {
            yTrend = -kraft;
            sprungStart = true;
            return true;
        }
        return false;
    }

    /**
     * Setzt, ob das Ziel-Objekt von der Schwerkraft beeinflusst wird und somit fallen, aber auch
     * springen kann.
     * @param   aktiv   Ist dieser Wert <code>true</code>, so wird das Ziel-Objekt von Schwerkraft
     * beeinflusst. Ist er <code>false</code>, dann nicht.
     */
    @Override
    public void schwerkraftAktivSetzen(boolean aktiv) {
        hatSchwerkraft = aktiv;
    }

    /**
     * Diese Methode setzt die kritische Tiefe eines Aktiv-Objektes. Ab dieser wird das entsprechende <code>FallReagierbar</code>-Inteface,
     * <b>das angemeldet wurde</b>, ueber den Fall informiert.
     * @param tiefe Die Tiefe, ab der das anliegende <code>FallReagierbar</code>-Interface informiert werden soll. Als Y-Koordinate.
     * @see #fallReagierbarAnmelden(FallReagierbar, int)
     */
    @Override
    public void kritischeTiefeSetzen(int tiefe) {
        kritischeTiefe = tiefe;
    }

    /**
     * In dieser Methode wird der <code>FallReagierbar</code>-Listener angemeldet.<br />
     * Dieser wird ab sofort <i>immer wieder</i> informiert, solange das Ziel-<code>Raum</code>-Objekt unterhalb der
     * Toleranzgrenze liegt.
     * @param f     Das <code>FallReagierbar</code>-Objekt, das ab sofort im Grenzfall informiert wird.
     * @param tiefe Die kritische Tiefe, ab der das Interface informiert wird.
     * @see #kritischeTiefeSetzen(int tiefe)
     */
    @Override
    public void fallReagierbarAnmelden(FallReagierbar f, int tiefe) {
        fListener = f;
        kritischeTiefe = tiefe;
    }

    /**
     * In dieser Methode wird der <code>StehReagierbar</code>-Listener angemeldet.<br />
     * Dieser wird ab sofort immer dann <i>einmalig</i> informiert, wenn das Ziel-<code>Raum</code>-Objekt nach einem
     * Sprung/Fall wieder auf einem Passiv-Objekt steht.
     * @param s Das <code>StehReagierbar</code>-Objekt, das ab sofort immer einmalig informiert wird, wenn das Ziel-Objekt
     * zum Stehen kommt.
     */
    public void stehReagierbarAnmelden(StehReagierbar s) {
        sListener = s;
    }

    /**
     * Testet, ob das hieran anliegende Ziel-Objekt steht.
     * @return  <code>true</code>, wenn das Ziel-Objekt auf einem Passiv-Objekt steht und
     * somit nicht faellt oder steigt, sonst <code>false</code>.<br />
     * <b>Bewegung nach Rechts/Links wird hier nicht beruecksichtigt.</b>
     */
    @Override
    public boolean steht() {
        BoundingRechteck test = ziel.dimension().verschobeneInstanz(new Vektor(0, 1));
        return physik.inPassivem(test);
    }

    
    /**
     * Setzt die Schwerkraft fuer dieses Objekt.<br />
     * <b>Achtung:</b>
     * Standardwert: 4<br />
     * Groesserer Wert = langsamer Fallen<br />
     * Kleinerer Wert = schneller Fallen <br />
     * Negativer Wert : Moege Gott uns allen gnaedig sein...
     * @param schwerkraft Der Wert fuer die Schwerkraft der Physik.<br />
     * <b>Wichtig:</b> Dies repraesentiert <i>keinen</i> Wert fuer die (Erd-)
     * Beschleunigungszahl "g" aus der Physik. Schon allein deshalb, weil die 
     * Zahl umgekehrt wirkt (s. oben).
     * @see ea.Raum#aktivMachen()
     */
    @Override
    public void schwerkraftSetzen(int schwerkraft) {
        this.schwerkraft = schwerkraft;
    }

    /**
     * {@inheritDoc}
     */
	@Override
	public void impulsHinzunehmen(Vektor impuls) {
		Logger.error("Aktivobjekte unterstützen leider keine Impulsrechnung. Dafür gibt es die Newton-Körper!");
	}

	/**
     * {@inheritDoc}
     */
	@Override
	public void geschwindigkeitHinzunehmen(Vektor geschwindigkeit) {
		Logger.error("Aktivobjekte unterstützen leider keine Geschwindigkeit. Dafür gibt es die Newton-Körper!");
	}

	/**
     * {@inheritDoc}
     */
	@Override
	public float getLuftwiderstandskoeffizient() {
		Logger.error("Aktivobjekte unterstützen leider keinen Luftwiderstand. Dafür gibt es die Newton-Körper!");
		return 0;
	}

	/**
     * {@inheritDoc}
     */
	@Override
	public boolean istBeeinflussbar() {
		Logger.error("Aktivobjekte unterstützen leider keinen Beeinflussbarkeit. Dafür gibt es die Newton-Körper!");
		return false;
	}

	/**
     * {@inheritDoc}
     */
	@Override
	public float getMasse() {
		Logger.error("Aktivobjekte unterstützen leider keine Masse. Dafür gibt es die Newton-Körper!");
		return 0;
	}

	/**
     * {@inheritDoc}
     */
	@Override
	public Vektor getForce() {
		Logger.error("Aktivobjekte unterstützen leider keine Kraftrechnung. Dafür gibt es die Newton-Körper!");
		return null;
	}

	/**
     * {@inheritDoc}
     */
	@Override
	public void luftwiderstandskoeffizientSetzen(float luftwiderstandskoeffizient) {
		Logger.error("Aktivobjekte unterstützen leider keinen Luftwiderstand. Dafür gibt es die Newton-Körper!");
	}

	/**
     * {@inheritDoc}
     */
	@Override
	public void beeinflussbarSetzen(boolean beeinflussbar) {
		Logger.error("Aktivobjekte unterstützen leider keinen Beeinflussbarkeit. Dafür gibt es die Newton-Körper!");
	}

	/**
     * {@inheritDoc}
     */
	@Override
	public void masseSetzen(float masse) {
		Logger.error("Aktivobjekte unterstützen leider keine Masse. Dafür gibt es die Newton-Körper!");
	}

	/**
     * {@inheritDoc}
     */
	@Override
	public void kraftSetzen(Vektor kraft) {
		Logger.error("Aktivobjekte unterstützen leider keine Kraftrechnung. Dafür gibt es die Newton-Körper!");
	}

	/**
     * {@inheritDoc}
     */
	@Override
	public void geschwindigkeitSetzen(Vektor geschwindigkeit) {
		Logger.error("Aktivobjekte unterstützen leider keine Geschwindigkeit. Dafür gibt es die Newton-Körper!");
	}

	/**
     * {@inheritDoc}
     */
	@Override
	public void einfluesseZuruecksetzen() {
		Logger.error("Aktivobjekte unterstützen leider keine Einflüsse. Dafür gibt es die Newton-Körper!");
	}

	/**
     * {@inheritDoc}
     */
	@Override
	public void kraftAnwenden(Vektor kraft, float t_kraftuebertrag) {
		Logger.error("Aktivobjekte unterstützen leider keine Kraftrechnung. Dafür gibt es die Newton-Körper!");
	}
}