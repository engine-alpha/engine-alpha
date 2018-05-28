package ea.internal.phy;

import ea.*;
import ea.handle.Physics;
import ea.internal.util.Logger;
import ea.actor.ActorGroup;
import ea.actor.Actor;
import org.jbox2d.dynamics.Body;
import org.jbox2d.dynamics.BodyDef;
import org.jbox2d.dynamics.FixtureDef;

/**
 *
 * Created by andonie on 16.02.15.
 */
public class NullHandler extends PhysikHandler {

    /**
     * Die Fixture Definition für das Client-Objekt.
     * Enthält auch die <b>shape</b>-Informationen.
     */
    private final FixtureDef fixtureDef;

    /**
     * Masse als hilfeweise gespeicherte Variable.
     */
    private float masse;

    /**
     * Referenz auf die World, in der sich der Handler befindet.
     */
    private WorldHandler worldHandler;

    public NullHandler(Actor actor) {
        super(actor, Physics.Type.PASSIV, false);
        bodyDef = new BodyDef();


        //Fixture Definition mit Standard-Werten
        fixtureDef = new FixtureDef();
        fixtureDef.density = 30f;
        fixtureDef.friction = 0f;
        fixtureDef.restitution = 0.5f;

    }

    /**
     * Speichert die Position des Objekts. Ist zu Beginn immer der Ursprung.
     */
    private Point position = Point.CENTRE;

    /**
     * Speichert die aktuelle Rotation in Radians. Ist zu Beginn stets 0.
     */
    private float rotation = 0;

    /**
     * Die Standard Body-Definition. Wird beim Übertrag eines Null-Handlers in einen
     * JB2D-Handler verwendet.
     */
    private final BodyDef bodyDef;

    @Override
    public void verschieben(Vector v) {
        position = position.verschobeneInstanz(v);
    }

    @Override
    public Point mittelpunkt() {
        System.out.println("Nullhandler Mittelpunkt");
        return null;
    }

    @Override
    public boolean beinhaltet(Point p) {
        return false;
    }

    @Override
    public Point position() {
        return position;
    }

    @Override
    public float rotation() {
        return rotation;
    }

    @Override
    public void rotieren(float radians) {
        rotation += radians;
    }

    @Override
    public void dichteSetzen(float dichte) {
        fixtureDef.density = dichte;
    }

    @Override
    public float dichte() {

        return fixtureDef.density;
    }

    @Override
    public void reibungSetzen(float reibung) {
        fixtureDef.friction = reibung;
    }

    @Override
    public float reibung() {
        return fixtureDef.friction;
    }

    @Override
    public void elastizitaetSetzen(float ela) {
        fixtureDef.restitution = ela;
    }

    @Override
    public float elastizitaet() {
        return fixtureDef.restitution;
    }

    @Override
    public void masseSetzen(float masse) {
        this.masse = masse;
    }

    @Override
    public float masse() {

        return masse;
    }

    @Override
    public void kraftWirken(Vector kraft) {
        Logger.error("Physics", "Bevor Physics genutzt wird, muss das Objekt (direkt oder indirekt) mit einer Wurzel verbunden " +
                "sein.");
    }

    @Override
    public void drehMomentWirken(float drehmoment) {
        Logger.error("Physics", "Bevor Physics genutzt wird, muss das Objekt (direkt oder indirekt) mit einer Wurzel verbunden " +
                "sein.");
    }

    @Override
    public void drehImpulsWirken(float drehimpuls) {
        Logger.error("Physics", "Bevor Physics genutzt wird, muss das Objekt (direkt oder indirekt) mit einer Wurzel verbunden " +
                "sein.");
    }

    @Override
    public void schwerkraftSetzen(Vector schwerkraftInN) {
        Logger.error("Physics", "Bevor Physics genutzt wird, muss das Objekt (direkt oder indirekt) mit einer Wurzel verbunden " +
                "sein.");
    }

    @Override
    public void kraftWirken(Vector kraftInN, Point globalerOrt) {
            Logger.error("Physics", "Bevor Physics genutzt wird, muss das Objekt (direkt oder indirekt) mit einer Wurzel verbunden " +
                    "sein.");
    }

    @Override
    public void impulsWirken(Vector impulsInNS, Point globalerOrt) {
            Logger.error("Physics", "Bevor Physics genutzt wird, muss das Objekt (direkt oder indirekt) mit einer Wurzel verbunden " +
                    "sein.");
    }

    @Override
    public void killBody() {
        Logger.warning("Physics/INTERNAL WARNING", "Kill Body wurde an einem Null-Handler aufgerufen.");
    }

    @Override
    public WorldHandler worldHandler() {
        if(worldHandler == null) {
            Logger.error("Physics", "Ein Objekt wurde physikalisch angefragt, bevor es an einer Wurzel war.");
            return null;
        } return worldHandler;
    }

    @Override
    public Body getBody() {
        Logger.warning("Physics/Internal", "getBody()-Ausgabe wurde an Null-Handler aufgegeben.");
        return null;
    }

    @Override
    public void physicalReset() {
        //Nothing to do.
    }

    @Override
    public void geschwindigkeitSetzen(Vector geschwindigkeitInMProS) {
        Logger.error("Physics", "Bevor Physics genutzt wird, muss das Objekt (direkt oder indirekt) mit einer Wurzel verbunden " +
                "sein.");
    }

    @Override
    public Vector geschwindigkeit() {
        Logger.error("Physics", "Bevor Physics genutzt wird, muss das Objekt (direkt oder indirekt) mit einer Wurzel verbunden " +
                "sein.");
        return null;
    }

    @Override
    public void rotationBlockiertSetzen(boolean block) {
        Logger.error("Physics", "Bevor Physics genutzt wird, muss das Objekt (direkt oder indirekt) mit einer Wurzel verbunden " +
                "sein.");
    }

    @Override
    public boolean rotationBlockiert() {
        Logger.error("Physics", "Bevor Physics genutzt wird, muss das Objekt (direkt oder indirekt) mit einer Wurzel verbunden " +
                "sein.");
        return false;
    }

    /**
     *
     * @param type   Der neue Type.
     * @return
     */
    @Override
    public PhysikHandler typ(Physics.Type type) {

        //System.out.println("TYPE " + setType + " at " + this);

        if(type == null) {
            Logger.error("Physics", "Physics-Type wurde nicht spezifiziert.");
            return this;
        }
        bodyDef.type = type.convert();

        if(fixtureDef.shape == null) {
            //Das Objekt hat keine Shape (ist ActorGroup oder nicht an einem ActorGroup angemeldet)
            return this;
        }


        bodyDef.active = true;
        fixtureDef.isSensor = type == Physics.Type.PASSIV;// && isSensor;

        bodyDef.position.set(worldHandler.fromVektor(position.asVector()));
        bodyDef.gravityScale = type == Physics.Type.PASSIV ? 0 : 1;

        return new BodyHandler(actor, worldHandler, bodyDef, fixtureDef, physikType, isSensor, this);

    }


    @Override
    public void setSensor(boolean isSensor) {
        this.isSensor = isSensor;
    }

    @Override
    public void update(WorldHandler worldHandler) throws IllegalStateException {
        if(worldHandler == null)
            return;

        this.worldHandler = worldHandler;


        if(! (this.actor instanceof ActorGroup)) worldHandler.blockPPMChanges();
        fixtureDef.shape = actor.createShape(worldHandler.getPixelProMeter());

        actor.setBodyType(physikType);
    }
}
