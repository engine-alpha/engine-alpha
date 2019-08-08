package ea.edu;

import ea.Vector;
import ea.actor.Actor;
import ea.actor.BodyType;
import ea.animation.CircleAnimation;
import ea.animation.LineAnimation;
import ea.edu.event.KollisionsReagierbar;
import ea.internal.annotations.API;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public abstract class EduActor<T extends Actor> {
    private static final Map<Actor, EduActor> actorMap = new ConcurrentHashMap<>();

    private final T actor;

    public EduActor(T actor) {
        this.actor = actor;

        // public Physics Setup für EDU Objekte
        this.actor.setRotationLocked(true);
        this.actor.setRestitution(0);

        actorMap.put(getActor(), this);

        Spiel.getActiveScene().addEduActor(this);
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

    /**
     * Setzt die Transparenz dieses Actors.
     *
     * @param transparenz Die Transparenz dieses Actors. Ein Wert von 0 entspricht voll sichtbar.
     *                    Ein Wert von 1 entspricht voll unsichtbar.
     *
     * @see #nenneTransparenz()
     */
    @API
    public void setzeTransparenz(float transparenz) {
        if (transparenz < 0 || transparenz > 1) {
            throw new IllegalArgumentException("Fehlerhafte Transparenzeingabe. Muss zwischen 0 und 1 sein. War " + transparenz);
        }

        this.actor.setOpacity(1 - transparenz);
    }

    /**
     * Gibt den Transparenzwert des EDU Actors aus.
     *
     * @return Der Transparenzwert des EDU Actors. Zwischen 0 und 1.
     *
     * @see #setzeTransparenz(float)
     */
    @API
    public float nenneTransparenz() {
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
    public void verschieben(float dX, float dY) {
        this.actor.moveBy(dX, dY);
    }

    @API
    public void drehen(float grad) {
        this.actor.rotateBy(grad);
    }

    @API
    public void setzeDrehwinkel(float grad) {
        this.actor.setRotation(grad);
    }

    @API
    public float nenneDrehwinkel() {
        return this.actor.getRotation();
    }

    @API
    public void setzeMittelpunkt(float mX, float mY) {
        this.actor.setCenter(mX, mY);
    }

    @API
    public Vector nenneMittelpunkt() {
        return this.actor.getCenter();
    }

    @API
    public float nenneMittelpunktX() {
        return this.actor.getCenter().x;
    }

    @API
    public float nenneMittelpunktY() {
        return this.actor.getCenter().y;
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
    public boolean beinhaltetPunkt(float pX, float pY) {
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
    public void wirkeImpuls(float iX, float iY) {
        this.actor.applyImpulse(new Vector(iX, iY));
    }

    @API
    public void setzeReibung(float reibungsKoeffizient) {
        this.actor.setFriction(reibungsKoeffizient);
    }

    @API
    public float nenneReibung() {
        return this.actor.getFriction();
    }

    @API
    public void setzeGeschwindigkeit(float vX, float vY) {
        this.actor.setVelocity(new Vector(vX, vY));
    }

    @API
    public float nenneGeschwindigkeitX() {
        return this.actor.getVelocity().x;
    }

    @API
    public float nenneGeschwindigkeitY() {
        return this.actor.getVelocity().y;
    }

    @API
    public void setzeElastizitaet(float elastizitaetsKoeffizient) {
        this.actor.setRestitution(elastizitaetsKoeffizient);
    }

    @API
    public float nenneElastizitaet() {
        return this.actor.getRestitution();
    }

    @API
    public float nenneMasse() {
        return this.actor.getMass();
    }

    @API
    public void setzeMasse(float masse) {
        this.actor.setMass(masse);
    }

    @API
    public boolean steht() {
        return this.actor.isGrounded();
    }

    @API
    public boolean stehtAuf(EduActor actor) {
        return this.actor.overlaps(actor.getActor()) && this.actor.isGrounded();
    }

    @API
    public void macheAktiv() {
        this.actor.setBodyType(BodyType.DYNAMIC);
    }

    @API
    public void machePassiv() {
        this.actor.setBodyType(BodyType.STATIC);
    }

    @API
    public void macheNeutral() {
        this.actor.setBodyType(BodyType.SENSOR);
    }

    @API
    public void machePartikel(float lebenszeit) {
        this.actor.animateParticle(lebenszeit);
    }

    @API
    public void springe(float staerke) {
        if (steht()) {
            this.actor.applyImpulse(new Vector(0, staerke * nenneMasse()));
        }
    }

    /**
     * Setzt die Ebene (z-Index) des Actors.
     *
     * @param position die Ebenennummer des Actors.
     *
     * @see #nenneEbenenPosition()
     */
    @API
    public void setzeEbenenPosition(int position) {
        this.actor.setLayerPosition(position);
    }

    /**
     * Gibt die Ebenennummer (z-Index) des Actors aus.
     *
     * @return die Ebenennummer
     *
     * @see #setzeEbenenPosition(int)
     */
    @API
    public int nenneEbenenPosition() {
        return this.actor.getLayerPosition();
    }

    /**
     * Animiert flüssig die Transparenz dieses Actors von einem bestimmten Wert zu einem bestimmten Wert.
     *
     * @param transparenzNach Die Endtransparenz
     * @param zeitInSekunden  Die Zeit in Sekunden, die vergehen, bis der Transparenzwert des Actors
     *                        von <code>transparenzVon</code> bis <code>transparenzNach</code> animiert.
     *
     * @see #setzeTransparenz(float)
     */
    @API
    public void animiereTransparenz(float zeitInSekunden, float transparenzNach) {
        if (transparenzNach < 0 || transparenzNach > 1) {
            throw new IllegalArgumentException("Transparenzen müssen stets zwischen 0 und 1 sein");
        }

        this.actor.animateOpacity(zeitInSekunden, 1 - transparenzNach);
    }

    /**
     * Bewegt den Actor in einem Kreis um einen Angegebenen Mittelpunkt.
     *
     * @param mX            X-Koordinate des Mittelpunkts der Revolution.
     * @param mY            Y-Koordinate des Mittelpunkts der Revolution.
     * @param sekunden      Dauer in Sekunden, die eine komplette Umdrehung benötigt.
     * @param uhrzeigersinn <code>true</code>= Drehung findet im Uhrzeigersinn statt. <code>false</code>: Gegen den
     *                      Uhrzeigersinn
     * @param rotation      <code>true</code>=Das Actor-Objekt wird auch entsprechend seiner Kreis-Position rotiert.
     *                      <code>false</code>=Das Actor-Objekt behält seine Rotation bei.
     */
    @API
    public void animiereKreis(float sekunden, float mX, float mY, boolean uhrzeigersinn, boolean rotation) {
        this.actor.addFrameUpdateListener(new CircleAnimation(getActor(), new Vector(mX, mY), sekunden, uhrzeigersinn, rotation));
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
     */
    @API
    public void animiereGerade(float sekunden, float zX, float zY, boolean loop) {
        this.actor.addFrameUpdateListener(new LineAnimation(getActor(), new Vector(zX, zY), sekunden, loop));
    }

    /**
     * Setzt die Kollisionsformen für das Objekt neu.
     *
     * @param kollisionsFormenCode Der Code, der die Kollisionsformen für dieses Objekt beschreibt.
     *
     * @see ea.internal.ShapeBuilder#fromString(String)
     */
    @API
    public void setzeKollisionsFormen(String kollisionsFormenCode) {
        this.actor.setShapes(kollisionsFormenCode);
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
    public void erzeugeStabverbindung(EduActor anderer, float aX, float aY, float bX, float bY) {
        this.actor.createDistanceJoint(anderer.getActor(), new Vector(aX, aY), new Vector(bX, bY));
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
    public void erzeugeGelenkverbindung(EduActor anderer, float aX, float aY) {
        this.actor.createRevoluteJoint(anderer.getActor(), new Vector(aX, aY));
    }

    /**
     * Baut einen Rope-Joint an diesem und einem weiteren Actor.
     *
     * @param anderer    Anderer Actor.
     * @param ropeLength Maximale Länge zwischen Punkt A und B zu jedem Zeitpunkt.
     * @param aX         Punkt A, Koordinate X
     * @param aY         Punkt A, Koordinate Y
     * @param bX         Punkt B, Koordinate X
     * @param bY         Punkt B, Koordinate Y
     *
     * @see ea.actor.Actor#createRopeJoint(Actor, Vector, Vector, float)
     */
    @API
    public void erzeugeSeilverbindung(EduActor anderer, float ropeLength, float aX, float aY, float bX, float bY) {
        this.actor.createRopeJoint(anderer.getActor(), new Vector(aX, aY), new Vector(bX, bY), ropeLength);
    }

    /**
     * Führt ein Runnable verzögert aus.
     *
     * @param verzoegerungInSekunden Spielzeit-Verzögerung
     * @param runnable               Runnable, dass dann ausgeführt wird
     */
    @API
    public void verzoegere(float verzoegerungInSekunden, Runnable runnable) {
        this.actor.delay(verzoegerungInSekunden, runnable);
    }
}
