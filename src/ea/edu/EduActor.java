package ea.edu;

import ea.Vector;
import ea.actor.Actor;
import ea.actor.BodyType;
import ea.animation.CircleAnimation;
import ea.animation.LineAnimation;
import ea.edu.event.KollisionsReagierbar;
import ea.internal.annotations.API;
import ea.internal.annotations.Internal;

public interface EduActor {

    /**
     * Gibt das Engine-interne Actor-Objekt zurück.
     *
     * @return Das Core-Engine-Actor-Objekt
     */
    @Internal
    Actor getActor();

    /**
     * Standard-Ausführung im Konstruktor. Meldet das Objekt unmittelbar in der aktuell aktiven Szene an.
     */
    @Internal
    default void eduSetup() {
        Spiel.getActiveScene().addEduActor(getActor());

        //Default Physics Setup für EDU Objekte
        getActor().setRotationLocked(true);
        getActor().setRestitution(0);
        Spiel.actorToInterfaceMap.put(getActor(), this);
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
    default void setzeTransparenz(float transparenz) {
        if (transparenz < 0 || transparenz > 1) {
            throw new IllegalArgumentException("Fehlerhafte Transparenzeingabe. Muss zwischen 0 und 1 sein. War " + transparenz);
        }
        getActor().setOpacity(1 - transparenz);
    }

    /**
     * Gibt den Transparenzwert des EDU Actors aus.
     *
     * @return Der Transparenzwert des EDU Actors. Zwischen 0 und 1.
     *
     * @see #setzeTransparenz(float)
     */
    @API
    default float nenneTransparenz() {
        return 1 - getActor().getOpacity();
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
    default void entfernen() {
        getActor().remove();
    }

    @API
    default void verschieben(float dX, float dY) {
        getActor().move(dX, dY);
    }

    @API
    default void drehen(float drehwinkelInWinkelgrad) {
        getActor().rotateBy(drehwinkelInWinkelgrad);
    }

    @API
    default float nenneWinkel() {
        return getActor().getRotation();
    }

    @API
    default void setzeMittelpunkt(float mX, float mY) {
        getActor().setCenter(mX, mY);
    }

    @API
    default void setzeSichtbar(boolean sichtbar) {
        getActor().setVisible(sichtbar);
    }

    @API
    default boolean nenneSichtbar() {
        return getActor().isVisible();
    }

    @API
    default float nenneMx() {
        return getActor().getCenter().x;
    }

    @API
    default float nenneMy() {
        return getActor().getCenter().y;
    }

    @API
    default boolean beinhaltetPunkt(float pX, float pY) {
        return getActor().contains(new Vector(pX, pY));
    }

    @API
    default Vector mittelPunkt() {
        return getActor().getCenter();
    }

    @API
    default Vector zentrum() {
        return mittelPunkt();
    }

    @API
    default boolean schneidet(EduActor other) {
        return other.getActor().overlaps(getActor());
    }

    /* ~~~ PHYSICS ~~~ */

    @API
    default void kollisionsReagierbarAnmelden(EduActor anderer, KollisionsReagierbar reagierbar) {
        this.getActor().addCollisionListener(anderer.getActor(), collisionEvent -> {
            if (!reagierbar.kollisionReagieren(anderer)) {
                collisionEvent.ignoreCollision();
            }
        });
    }

    @API
    default void kollisionsReagierbarAnmelden(KollisionsReagierbar reagierbar) {
        this.getActor().addCollisionListener(collisionEvent -> {
            EduActor other = Spiel.actorToInterfaceMap.get(collisionEvent.getColliding());
            if (!reagierbar.kollisionReagieren(other)) {
                collisionEvent.ignoreCollision();
            }
        });
    }

    @API
    default void setzeRotationBlockiert(boolean blockiert) {
        getActor().setRotationLocked(blockiert);
    }

    @API
    default void wirkeImpuls(float iX, float iY) {
        getActor().applyImpulse(new Vector(iX, iY));
    }

    @API
    default void setzeReibung(float reibungsKoeffizient) {
        getActor().setFriction(reibungsKoeffizient);
    }

    @API
    default void setzeGeschwindigkeit(float vX, float vY) {
        getActor().setVelocity(new Vector(vX, vY));
    }

    @API
    default void setzeElastizitaet(float elastizitaetsKoeffizient) {
        getActor().setRestitution(elastizitaetsKoeffizient);
    }

    @API
    default float nenneMasse() {
        return getActor().getMass();
    }

    @API
    default float nenneElastizitaet() {
        return getActor().getRestitution();
    }

    @API
    default float nenneReibung() {
        return getActor().getFriction();
    }

    @API
    default void setzeSchwerkraft(float schwerkraft) {
        getActor().getLayer().getParent().setGravity(new Vector(0, -schwerkraft));
    }

    @API
    default void setzeMasse(float masse) {
        getActor().setMass(masse);
    }

    @API
    default float nenneVx() {
        return getActor().getVelocity().x;
    }

    @API
    default float nenneVy() {
        return getActor().getVelocity().y;
    }

    /* ~~~ JUMP N RUN WRAPPER ~~~ */

    @API
    default boolean steht() {
        return getActor().isGrounded();
    }

    @API
    default boolean stehtAuf(EduActor actor) {
        return getActor().overlaps(actor.getActor()) && getActor().isGrounded();
    }

    @API
    default void macheAktiv() {
        getActor().setBodyType(BodyType.DYNAMIC);
    }

    @API
    default void machePassiv() {
        getActor().setBodyType(BodyType.STATIC);
    }

    @API
    default void macheNeutral() {
        getActor().setBodyType(BodyType.SENSOR);
    }

    @API
    default void machePartikel(float lebenszeit) {
        getActor().animateParticle(lebenszeit);
    }

    @API
    default void sprung(float staerke) {
        if (steht()) {
            getActor().applyImpulse(new Vector(0, staerke * 1000));
        }
    }

    /**
     * Bewegt den Actor anhand einer Gerade.
     *
     * @param zX       X-Koordinate des Mittelpunkts des Actors nach <code>ms</code> Millisekunden
     * @param zY       Y-Koordinate des Mittelpunkts des Actors nach <code>ms</code> Millisekunden
     * @param sekunden Zeit in Sekunden, die der Actor von Beginn der Animation benötigt, bis er am angegebenen
     *                 Endpunkt angekommen ist.
     * @param loop     <code>true</code>: Der Actor "ping pongt" zwischen dem impliziten Startpunkt und dem angegebenen
     *                 Endpunkt hin und her. Die Strecke in eine Richtung benötigt jeweils <code>ms</code>
     *                 Millisekunden
     *                 Zeit. <br>
     *                 <code>false</code>: Die Animation endet automatisch, nachdem der Zielpunkt (das erste Mal)
     *                 erreicht
     *                 wurde.
     */
    @API
    default void geradenAnimation(float zX, float zY, float sekunden, boolean loop) {
        getActor().addFrameUpdateListener(new LineAnimation(getActor(), new Vector(zX, zY), sekunden, loop));
    }

    /**
     * Setzt die Ebene (z-Index) des Actors.
     *
     * @param ebenenNummer die Ebenennummer des actors.
     *
     * @see #nenneEbene()
     */
    @API
    default void setzeEbene(int ebenenNummer) {
        getActor().setLayerPosition(ebenenNummer);
    }

    /**
     * Gibt die Ebenennummer (z-Index) des Actors aus.
     *
     * @return die Ebenennummer
     *
     * @see #setzeEbene(int)
     */
    @API
    default int nenneEbene() {
        return getActor().getLayerPosition();
    }



    /* ____________________ ANIMATION  ____________________*/

    /**
     * Animiert flüssig die Transparenz dieses Actors von einem bestimmten Wert zu einem bestimmten Wert.
     *
     * @param transparenzVon  Die Starttransparenz
     * @param transparenzNach Die Endtransparenz
     * @param zeitInSekunden  Die Zeit in Sekunden, die vergehen, bis der Transparenzwert des Actors
     *                        von <code>transparenzVon</code> bis <code>transparenzNach</code> animiert.
     *
     * @see #setzeTransparenz(float)
     */
    @API
    default void animiereTransparenz(float transparenzVon, float transparenzNach, float zeitInSekunden) {
        if (transparenzVon < 0 || transparenzNach < 0 || transparenzVon > 1 || transparenzNach > 1) {
            throw new IllegalArgumentException("Transparenzen müssen stets zwischen 0 und 1 sein.");
        }
        getActor().setOpacity(1 - transparenzVon);
        getActor().animateOpacity(zeitInSekunden, 1 - transparenzNach);
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
    default void kreisAnimation(float mX, float mY, float sekunden, boolean uhrzeigersinn, boolean rotation) {
        getActor().addFrameUpdateListener(new CircleAnimation(getActor(), new Vector(mX, mY), sekunden, uhrzeigersinn, rotation));
    }

    /**
     * Setzt die Kollisionsformen für das Objekt neu.
     *
     * @param kollisionsFormenCode Der Code, der die Kollisionsformen für dieses Objekt beschreibt.
     *
     * @see ea.internal.ShapeBuilder#fromString(String)
     */
    @API
    default void setzeKollisionsFormen(String kollisionsFormenCode) {
        getActor().setShapes(kollisionsFormenCode);
    }


    /* ____________________ JOINTS  ____________________*/

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
    default void erzeugeStabVerbindung(EduActor anderer, float aX, float aY, float bX, float bY) {
        getActor().createDistanceJoint(anderer.getActor(), new Vector(aX, aY), new Vector(bX, bY));
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
    default void erzeugeGelenkVerbindung(EduActor anderer, float aX, float aY) {
        getActor().createRevoluteJoint(anderer.getActor(), new Vector(aX, aY));
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
    default void erzeugeSeilVerbindung(EduActor anderer, float ropeLength, float aX, float aY, float bX, float bY) {
        getActor().createRopeJoint(anderer.getActor(), new Vector(aX, aY), new Vector(bX, bY), ropeLength);
    }
}
