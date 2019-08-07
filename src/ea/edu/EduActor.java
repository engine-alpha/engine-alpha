package ea.edu;

import ea.Vector;
import ea.actor.Actor;
import ea.animation.CircleAnimation;
import ea.animation.LineAnimation;
import ea.handle.BodyType;
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
        getActor().physics.setRotationLocked(true);
        getActor().physics.setRestitution(0);
    }

    @API
    default void entfernen() {
        Spiel.getActiveScene().remove(getActor());
    }

    @API
    default void verschieben(float dX, float dY) {
        getActor().position.move(dX, dY);
    }

    @API
    default void drehen(float drehwinkelInWinkelgrad) {
        getActor().position.rotate(drehwinkelInWinkelgrad);
    }

    @API
    default float nenneWinkel() {
        return getActor().position.getRotation();
    }

    @API
    default void setzeMittelpunkt(float mX, float mY) {
        getActor().position.setCenter(mX, mY);
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
        return getActor().position.getCenter().x;
    }

    @API
    default float nenneMy() {
        return getActor().position.getCenter().y;
    }

    @API
    default boolean beinhaltetPunkt(float pX, float pY) {
        return getActor().contains(new Vector(pX, pY));
    }

    @API
    default Vector mittelPunkt() {
        return getActor().position.getCenter();
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
    default void setzeRotationBlockiert(boolean blockiert) {
        getActor().physics.setRotationLocked(blockiert);
    }

    @API
    default void wirkeImpuls(float iX, float iY) {
        getActor().physics.applyImpulse(new Vector(iX, iY));
    }

    @API
    default void setzeReibung(float reibungsKoeffizient) {
        getActor().physics.setFriction(reibungsKoeffizient);
    }

    @API
    default void setzeGeschwindigkeit(float vX, float vY) {
        getActor().physics.setVelocity(new Vector(vX, vY));
    }

    @API
    default void setzeElastizitaet(float elastizitaetsKoeffizient) {
        getActor().physics.setRestitution(elastizitaetsKoeffizient);
    }

    @API
    default void setzeSchwerkraft(float schwerkraft) {
        getActor().getScene().setGravity(new Vector(0, -schwerkraft));
    }

    @API
    default void setzeMasse(float masse) {
        getActor().physics.setMass(masse);
    }

    @API
    default float nenneVx() {
        return getActor().physics.getVelocity().x;
    }

    @API
    default float nenneVy() {
        return getActor().physics.getVelocity().y;
    }

    /* ~~~ JUMP N RUN WRAPPER ~~~ */

    @API
    default boolean steht() {
        return getActor().physics.isGrounded();
    }

    @API
    default boolean stehtAuf(Actor actor) {
        return getActor().overlaps(actor) && getActor().physics.isGrounded();
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
        getActor().setBodyType(BodyType.PASSIVE);
    }

    @API
    default void sprung(float staerke) {
        if (steht()) {
            getActor().physics.applyImpulse(new Vector(0, staerke * 1000));
        }
    }

    /**
     * Bewegt den Actor anhand einer Gerade.
     *
     * @param zX   X-Koordinate des Mittelpunkts des Actors nach <code>ms</code> Millisekunden
     * @param zY   Y-Koordinate des Mittelpunkts des Actors nach <code>ms</code> Millisekunden
     * @param ms   Zeit in Millisekunden, die der Actor von Beginn der Animation benötigt, bis er am angegebenen
     *             Endpunkt angekommen ist.
     * @param loop <code>true</code>: Der Actor "ping pongt" zwischen dem impliziten Startpunkt und dem angegebenen
     *             Endpunkt hin und her. Die Strecke in eine Richtung benötigt jeweils <code>ms</code> Millisekunden
     *             Zeit. <br>
     *             <code>false</code>: Die Animation endet automatisch, nachdem der Zielpunkt (das erste Mal) erreicht
     *             wurde.
     */
    @API
    default void geradenAnimation(float zX, float zY, int ms, boolean loop) {
        Spiel.getActiveScene().addFrameUpdateListener(new LineAnimation(getActor(), new Vector(zX, zY), ms, loop));
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
        getActor().setLayer(ebenenNummer);
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
        return getActor().getLayer();
    }

    /**
     * Bewegt den Actor in einem Kreis um einen Angegebenen Mittelpunkt.
     *
     * @param mX            X-Koordinate des Mittelpunkts der Revolution.
     * @param mY            Y-Koordinate des Mittelpunkts der Revolution.
     * @param ms            Dauer in Millisekunden, die eine komplette Umdrehung benötigt.
     * @param uhrzeigersinn <code>true</code>= Drehung findet im Uhrzeigersinn statt. <code>false</code>: Gegen den
     *                      Uhrzeigersinn
     * @param rotation      <code>true</code>=Das Actor-Objekt wird auch entsprechend seiner Kreis-Position rotiert.
     *                      <code>false</code>=Das Actor-Objekt behält seine Rotation bei.
     */
    @API
    default void kreisAnimation(float mX, float mY, int ms, boolean uhrzeigersinn, boolean rotation) {
        Spiel.getActiveScene().addFrameUpdateListener(new CircleAnimation(getActor(), new Vector(mX, mY), ms, uhrzeigersinn, rotation));
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
     * @see ea.handle.Physics#createDistanceJoint(Actor, Vector, Vector)
     */
    @API
    default void distanceJointEinsetzen(EduActor anderer, float aX, float aY, float bX, float bY) {
        getActor().physics.createDistanceJoint(anderer.getActor(), new Vector(aX, aY), new Vector(bX, bY));
    }

    /**
     * Baut einen Revolute-Joint an diesem und einem weiteren Actor.
     *
     * @param anderer Anderer Actor.
     * @param aX      X Koordinate des Rotationspunktes
     * @param aY      Y Koordinate des Rotationspunktes
     *
     * @see ea.handle.Physics#createRevoluteJoint(Actor, Vector) e
     */
    @API
    default void revoluteJointEinsetzen(EduActor anderer, float aX, float aY) {
        getActor().physics.createRevoluteJoint(anderer.getActor(), new Vector(aX, aY));
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
     * @see ea.handle.Physics#createRopeJoint(Actor, Vector, Vector, float)
     */
    @API
    default void ropeJointEinsetzen(EduActor anderer, float ropeLength, float aX, float aY, float bX, float bY) {
        getActor().physics.createRopeJoint(anderer.getActor(), new Vector(aX, aY), new Vector(bX, bY), ropeLength);
    }
}
