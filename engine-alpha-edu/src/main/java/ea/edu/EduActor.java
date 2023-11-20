package ea.edu;

import ea.Vector;
import ea.actor.*;
import ea.animation.CircleAnimation;
import ea.animation.LineAnimation;
import ea.edu.event.KollisionsReagierbar;
import ea.edu.internal.EduScene;
import ea.event.AggregateFrameUpdateListener;
import ea.internal.FixtureBuilder;
import ea.internal.annotations.API;
import ea.internal.annotations.Internal;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public abstract class EduActor<T extends Actor> {
    private static final Map<Actor, EduActor> actorMap = new ConcurrentHashMap<>();

    private final T actor;

    private final EduScene eduScene;

    private boolean animationsPaused = false;

    private AggregateFrameUpdateListener lastAnimation = null;

    public EduActor(T actor) {
        this.actor = actor;

        // Default Physics Setup für EDU Objekte
        this.actor.setRotationLocked(true);
        this.actor.setRestitution(0);
        this.actor.setCenter(0, 0);

        this.actor.addMountListener(() -> actorMap.put(this.actor, this));
        this.actor.addUnmountListener(() -> actorMap.remove(this.actor));

        eduScene = EduSetup.getActiveScene();
        EduSetup.setup(this, eduScene);
    }

    /**
     * Gibt das Engine-interne Actor-Objekt zurück.
     *
     * @return Das Core-Engine-Actor-Objekt
     */
    @API
    protected final T getActor() {
        return this.actor;
    }

    @Internal
    private void addAnimation(AggregateFrameUpdateListener animation) {
        animation.setPaused(animationsPaused);
        lastAnimation = animation;
        this.actor.addFrameUpdateListener(animation);
    }

    /**
     * Setzt die Transparenz dieses Actors.
     *
     * @param transparenz Die Transparenz dieses Actors. Ein Wert von 0 entspricht voll sichtbar.
     *                    Ein Wert von 1 entspricht voll unsichtbar.
     *
     * @see #nenneTransparenz()
     */
    @API
    public void setzeTransparenz(double transparenz) {
        if (transparenz < 0 || transparenz > 1) {
            throw new IllegalArgumentException("Fehlerhafte Transparenzeingabe. Muss zwischen 0 und 1 sein. War " + transparenz);
        }

        this.actor.setOpacity((float) (1 - transparenz));
    }

    /**
     * Gibt den Transparenzwert des EDU Actors aus.
     *
     * @return Der Transparenzwert des EDU Actors. Zwischen 0 und 1.
     *
     * @see #setzeTransparenz(double)
     */
    @API
    public double nenneTransparenz() {
        return 1 - this.actor.getOpacity();
    }

    /**
     * Entfernt den Actor von allen Funktionen der Engine:
     * <ul>
     * <li>Der Actor ist nicht mehr sichtbar und wird nicht mehr gerendert.</li>
     * <li>Der Physics-Body des Actors wird entfernt und hat keinen Einfluss mehr auf die Physics.</li>
     * <li>Alle Listener, die mit diesem Actor zusammenhängen, werden nicht mehr informiert.</li>
     * </ul>
     */
    @API
    public void entfernen() {
        this.actor.remove();
    }

    @API
    public void verschieben(double dX, double dY) {
        this.actor.moveBy(new Vector(dX, dY));
    }

    @API
    public void drehen(double grad) {
        this.actor.rotateBy((float) grad);
    }

    @API
    public void setzeDrehwinkel(double grad) {
        this.actor.setRotation((float) grad);
    }

    @API
    public double nenneWinkelgeschwindigkeit() {
        return this.actor.getAngularVelocity();
    }

    @API
    public double nenneDrehwinkel() {
        return this.actor.getRotation();
    }

    @API
    public void setzeMittelpunkt(double mX, double mY) {
        this.actor.setCenter(new Vector(mX, mY));
    }

    @API
    public double nenneMittelpunktX() {
        return this.actor.getCenter().getX();
    }

    @API
    public double nenneMittelpunktY() {
        return this.actor.getCenter().getY();
    }

    @API
    public void setzeSichtbar(boolean sichtbar) {
        this.actor.setVisible(sichtbar);
    }

    @API
    public boolean istSichtbar() {
        return this.actor.isVisible();
    }

    @API
    public boolean beinhaltetPunkt(double pX, double pY) {
        return this.actor.contains(new Vector(pX, pY));
    }

    @API
    public boolean schneidet(EduActor objekt) {
        return objekt.actor.overlaps(getActor());
    }

    @API
    public <X extends EduActor> void registriereKollisionsReagierbar(X anderer, KollisionsReagierbar<X> kollisionsReagierbar) {
        this.actor.addCollisionListener(anderer.getActor(), collisionEvent -> {
            if (!kollisionsReagierbar.kollisionReagieren(anderer)) {
                collisionEvent.ignoreCollision();
            }
        });
    }

    @API
    public void registriereKollisionsReagierbar(KollisionsReagierbar<EduActor> reagierbar) {
        this.actor.addCollisionListener(collisionEvent -> {
            EduActor other = actorMap.get(collisionEvent.getColliding());
            if (!reagierbar.kollisionReagieren(other)) {
                collisionEvent.ignoreCollision();
            }
        });
    }

    @API
    public void setzeRotationBlockiert(boolean blockiert) {
        this.actor.setRotationLocked(blockiert);
    }

    @API
    public void wirkeImpuls(double iX, double iY) {
        this.actor.applyImpulse(new Vector(iX, iY));
    }

    @API
    public void setzeWinkelgeschwindigkeit(double umdrehnungenProSekunde) {
        this.actor.setAngularVelocity((float) umdrehnungenProSekunde);
    }

    @API
    public void setzeReibung(double reibungsKoeffizient) {
        this.actor.setFriction((float) reibungsKoeffizient);
    }

    @API
    public double nenneReibung() {
        return this.actor.getFriction();
    }

    @API
    public void setzeGeschwindigkeit(double vX, double vY) {
        this.actor.setVelocity(new Vector(vX, vY));
    }

    @API
    public double nenneGeschwindigkeitX() {
        return this.actor.getVelocity().getX();
    }

    @API
    public double nenneGeschwindigkeitY() {
        return this.actor.getVelocity().getY();
    }

    @API
    public void setzeElastizitaet(double elastizitaetsKoeffizient) {
        this.actor.setRestitution((float) elastizitaetsKoeffizient);
    }

    @API
    public double nenneElastizitaet() {
        return this.actor.getRestitution();
    }

    @API
    public double nenneMasse() {
        return this.actor.getMass();
    }

    @API
    public void setzeDichte(double dichteInKgProQM) {
        this.actor.setDensity((float) dichteInKgProQM);
    }

    @API
    public double nenneDichte() {
        return this.actor.getDensity();
    }

    @API
    public boolean steht() {
        return this.actor.isGrounded();
    }

    @API
    public boolean stehtAuf(EduActor actor) {
        return this.actor.overlaps(actor.getActor()) && this.actor.isGrounded();
    }

    /**
     * @see #macheDynamisch() ()
     * @deprecated Bezeichnung ist nicht identisch mit Core Engine. Wird in zukünftigem Release entfernt.
     */
    @Deprecated
    @API
    public void macheAktiv() {
        this.actor.setBodyType(BodyType.DYNAMIC);
    }

    @API
    public void macheDynamisch() {
        this.actor.setBodyType(BodyType.DYNAMIC);
    }

    /**
     * @see #macheStatisch()
     * @deprecated Bezeichnung ist nicht identisch mit Core Engine. Wird in zukünftigem Release entfernt.
     */
    @Deprecated
    @API
    public void machePassiv() {
        this.actor.setBodyType(BodyType.STATIC);
    }

    @API
    public void macheStatisch() {
        this.actor.setBodyType(BodyType.STATIC);
    }

    /**
     * @see #macheSensor() ()
     * @deprecated Bezeichnung ist nicht identisch mit Core Engine. Wird in zukünftigem Release entfernt.
     */
    @Deprecated
    @API
    public void macheNeutral() {
        this.actor.setBodyType(BodyType.SENSOR);
    }

    @API
    public void macheSensor() {
        this.actor.setBodyType(BodyType.SENSOR);
    }

    @API
    public void macheKinematisch() {
        this.actor.setBodyType(BodyType.KINEMATIC);
    }

    @API
    public void machePartikel(double lebenszeit) {
        this.actor.animateParticle((float) lebenszeit);
    }

    @API
    public void springe(double staerke) {
        if (steht()) {
            this.actor.applyImpulse(new Vector(0, staerke * nenneMasse()));
        }
    }

    /**
     * Setzt die Ebene (z-Index) des Actors.
     *
     * @param position die Ebenennummer des Actors.
     *
     * @see #nenneEbenenposition()
     */
    @API
    public void setzeEbenenposition(int position) {
        this.actor.setLayerPosition(position);
    }

    /**
     * Gibt die Ebenennummer (z-Index) des Actors aus.
     *
     * @return die Ebenennummer
     *
     * @see #setzeEbenenposition(int)
     */
    @API
    public int nenneEbenenposition() {
        return this.actor.getLayerPosition();
    }

    /**
     * Animiert flüssig die Transparenz dieses Actors von einem bestimmten Wert zu einem bestimmten Wert.
     *
     * @param nachTransparenz Die Endtransparenz
     * @param zeitInSekunden  Die Zeit in Sekunden, die vergehen, bis der Transparenzwert des Actors
     *                        von <code>transparenzVon</code> bis <code>transparenzNach</code> animiert.
     *
     * @see #setzeTransparenz(double)
     */
    @API
    public void animiereTransparenz(double zeitInSekunden, double nachTransparenz) {
        if (nachTransparenz < 0 || nachTransparenz > 1) {
            throw new IllegalArgumentException("Transparenzen müssen stets zwischen 0 und 1 sein");
        }

        this.actor.animateOpacity((float) zeitInSekunden, (float) (1 - nachTransparenz));
    }

    /**
     * Bewegt den Actor in einem Kreis um einen angegebenen Mittelpunkt.
     *
     * @param mX            X-Koordinate des Mittelpunkts der Revolution.
     * @param mY            Y-Koordinate des Mittelpunkts der Revolution.
     * @param sekunden      Dauer in Sekunden, die eine komplette Umdrehung benötigt.
     * @param uhrzeigersinn <code>true</code>= Drehung findet im Uhrzeigersinn statt. <code>false</code>: Gegen den
     *                      Uhrzeigersinn
     * @param rotation      <code>true</code>=Das Actor-Objekt wird auch entsprechend seiner Kreis-Position rotiert.
     *                      <code>false</code>=Das Actor-Objekt behält seine Rotation bei.
     *
     * @see #pausiereAnimation(boolean)
     */
    @API
    public void animiereKreis(double sekunden, double mX, double mY, boolean uhrzeigersinn, boolean rotation) {
        addAnimation(new CircleAnimation(getActor(), new Vector(mX, mY), (float) sekunden, uhrzeigersinn, rotation));
    }

    /**
     * Bewegt den Actor anhand einer Gerade.
     *
     * @param zX       X-Koordinate des Mittelpunkts des Actors nach <code>s</code> Sekunden
     * @param zY       Y-Koordinate des Mittelpunkts des Actors nach <code>s</code> Sekunden
     * @param sekunden Zeit in Sekunden, die der Actor von Beginn der Animation benötigt, bis er am angegebenen
     *                 Endpunkt angekommen ist.
     * @param loop     <code>true</code>: Der Actor "ping pongt" zwischen dem impliziten Startpunkt und dem angegebenen
     *                 Endpunkt hin und her. Die Strecke in eine Richtung benötigt jeweils <code>s</code>
     *                 Sekunden
     *                 Zeit. <br>
     *                 <code>false</code>: Die Animation endet automatisch, nachdem der Zielpunkt (das erste Mal)
     *                 erreicht
     *                 wurde.
     *
     * @see #pausiereAnimation(boolean)
     */
    @API
    public void animiereGerade(double sekunden, double zX, double zY, boolean loop) {
        addAnimation(new LineAnimation(getActor(), new Vector(zX, zY), (float) sekunden, loop));
    }

    /**
     * Setzt, ob die Animationen dieses Actors gerade laufen sollen oder nicht.
     *
     * @param pausiert <code>true</code>: Alle Animationen werden pausiert.
     *                 <code>false</code>: Alle Animationen laufen normal.
     *
     * @see #animiereGerade(double, double, double, boolean)
     * @see #animiereKreis(double, double, double, boolean, boolean)
     */
    @API
    public void pausiereAnimation(boolean pausiert) {
        animationsPaused = pausiert;
        if (lastAnimation != null) {
            lastAnimation.setPaused(pausiert);
        }
    }

    /**
     * Gibt an, ob die Animationen dieses Actors gerade pausiert sind.
     *
     * @return <code>true</code>: Alle Animationen sind pausiert.
     * <code>false</code>: Alle Animationen laufen.
     */
    @API
    public boolean nennePausiert() {
        return animationsPaused;
    }

    /**
     * Setzt die Kollisionsformen für das Objekt neu.
     *
     * @param kollisionsFormenCode Der Code, der die Kollisionsformen für dieses Objekt beschreibt.
     *
     * @see FixtureBuilder#fromString(String)
     */
    @API
    public void setzeKollisionsformen(String kollisionsFormenCode) {
        this.actor.setFixtures(kollisionsFormenCode);
    }

    /**
     * Setzt einen Distance Joint an diesem und einem weiteren Actor.
     *
     * @param anderer Anderer Actor.
     * @param aX      Punkt A, Koordinate X
     * @param aY      Punkt A, Koordinate Y
     * @param bX      Punkt B, Koordinate X
     * @param bY      Punkt B, Koordinate Y
     *
     * @see ea.actor.Actor#createDistanceJoint(Actor, Vector, Vector)
     */
    @API
    public StabVerbindung erzeugeStabverbindung(EduActor anderer, double aX, double aY, double bX, double bY) {
        DistanceJoint joint = this.actor.createDistanceJoint(anderer.getActor(), new Vector(aX, aY), new Vector(bX, bY));
        return new StabVerbindung(joint);
    }

    /**
     * Baut einen Revolute-Joint an diesem und einem weiteren Actor.
     *
     * @param anderer Anderer Actor.
     * @param aX      X Koordinate des Rotationspunktes
     * @param aY      Y Koordinate des Rotationspunktes
     *
     * @see ea.actor.Actor#createRevoluteJoint(Actor, Vector) e
     */
    @API
    public GelenkVerbindung erzeugeGelenkverbindung(EduActor anderer, double aX, double aY) {
        RevoluteJoint joint = this.actor.createRevoluteJoint(anderer.getActor(), new Vector(aX, aY));
        return new GelenkVerbindung(joint);
    }

    /**
     * Baut einen Rope-Joint an diesem und einem weiteren Actor.
     *
     * @param anderer Anderer Actor.
     * @param laenge  Maximale Länge zwischen Punkt A und B zu jedem Zeitpunkt.
     * @param aX      Punkt A, Koordinate X
     * @param aY      Punkt A, Koordinate Y
     * @param bX      Punkt B, Koordinate X
     * @param bY      Punkt B, Koordinate Y
     *
     * @see ea.actor.Actor#createRopeJoint(Actor, Vector, Vector, float)
     */
    @API
    public SeilVerbindung erzeugeSeilverbindung(EduActor anderer, double aX, double aY, double bX, double bY, double laenge) {
        RopeJoint joint = this.actor.createRopeJoint(anderer.getActor(), new Vector(aX, aY), new Vector(bX, bY), (float) laenge);
        return new SeilVerbindung(joint);
    }

    /**
     * Führt ein Runnable verzögert aus.
     *
     * @param verzoegerungInSekunden Spielzeit-Verzögerung in Sekunden
     * @param runnable               Runnable, das dann ausgeführt wird
     */
    @API
    public void verzoegere(double verzoegerungInSekunden, Runnable runnable) {
        this.actor.delay((float) verzoegerungInSekunden, runnable);
    }
}
