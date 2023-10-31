/*
 * Engine Alpha ist eine anfängerorientierte 2D-Gaming Engine.
 *
 * Copyright (c) 2011 - 2014 Michael Andonie and contributors.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */

package ea;

import ea.actor.*;
import ea.internal.annotations.API;
import ea.internal.annotations.Internal;

import java.awt.Color;
import java.awt.event.KeyEvent;
import java.net.JarURLConnection;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

/**
 * Diese Klasse definiert Versions-Konstanten und sorgt für eine About-Box beim Ausführen der
 * .jar-Datei.
 *
 * @author Niklas Keller {@literal <me@kelunik.com>}
 */
@Internal
public final class EngineAlpha {

    @SuppressWarnings ( "MagicNumber" )
    public static void main(String[] args) {
        Game.start(800, 600, new Scene() {
            private final List<Actor> items = new ArrayList<>();

            {
                setGravity(new Vector(0, -9.81f));

                Rectangle ground = new Rectangle(20, .2f);
                ground.setCenter(0, -6);
                ground.setRestitution(.9f);
                ground.setFriction(.2f);
                ground.setBodyType(BodyType.STATIC);
                add(ground);

                for (int i = 0; i < 3; i++) {
                    Rectangle a = new Rectangle(1, 1);
                    a.setPosition(-5, -2);
                    a.setRestitution(.9f);
                    a.setFriction(1);
                    a.setBodyType(BodyType.DYNAMIC);
                    a.setColor(new Color(26, 113, 156));
                    a.setRotation(30);
                    spawnItem(a);

                    Circle b = new Circle(1);
                    b.setPosition(5, -1);
                    b.setRestitution(.9f);
                    b.setFriction(1);
                    b.setBodyType(BodyType.DYNAMIC);
                    b.setColor(new Color(158, 5, 5));
                    b.applyImpulse(new Vector(Random.range(-100, 100), 0));
                    spawnItem(b);

                    Polygon c = new Polygon(new Vector(0, 0), new Vector(1, 0), new Vector(.5, 1));
                    c.setRestitution(.9f);
                    c.setFriction(1);
                    c.setBodyType(BodyType.DYNAMIC);
                    c.setColor(new Color(25, 159, 69));
                    c.setRotation(-20);
                    spawnItem(c);
                }

                Date date = new Date(BUILD_TIME * 1000);
                SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss z");
                sdf.setTimeZone(TimeZone.getTimeZone("UTC"));

                Text text = new Text("Build #" + VERSION_CODE + "   " + sdf.format(date), .5f);
                text.setPosition(-10, -7f);
                text.setColor(Color.WHITE);
                add(text);

                addFrameUpdateListener(time -> {
                    for (Actor item : items) {
                        if (item.getCenter().getY() < -10) {
                            spawnItem(item);
                        }
                    }
                });

                addKeyListener(e -> {
                    if (e.getKeyCode() == KeyEvent.VK_D) {
                        Game.setDebug(!Game.isDebug());
                    }
                });
            }

            private void spawnItem(Actor item) {
                if (!item.isMounted()) {
                    delay(Random.range(5), () -> {
                        items.add(item);
                        add(item);
                    });
                }

                item.resetMovement();
                item.setCenter(Random.range(-7, 7), Random.range(0, 5));
            }
        });

        Game.setExitOnEsc(true);
        Game.setTitle("Engine Alpha " + VERSION_STRING);
    }

    /**
     * Der Versionscode des aktuellen Release.<br>
     * Rechnung:<br>
     * <code>
     * 10000 * major + 100 * minor + 1 * bugfix
     * </code>
     */
    public static final int VERSION_CODE = 40000;

    /**
     * Format: v(major).(minor).(bugfix)
     * Beispiel: v3.1.2
     */
    public static final String VERSION_STRING = "v4.0.0-dev";

    /**
     * Gibt an, ob dieser Release in .jar - Form vorliegt. Ist das der Fall,
     * ist dieser Wert <code>true</code>, sonst ist er <code>false</code>.
     */
    public static final boolean IS_JAR;

    /**
     * Zeitpunkt, an dem diese Jar-Datei erzeugt wurde, falls als Jar-Datei ausgeführt, sonst die
     * aktuelle Zeit in Sekunden seit dem 01.01.1970 (Unix Timestamp)
     */
    public static final long BUILD_TIME;

    /*
     * Statischer Konstruktor.
     * Ermittelt <code>IS_JAR</code> und <code>BUILD_TIME</code>.
     */
    static {
        IS_JAR = isJar();
        BUILD_TIME = IS_JAR ? getBuildTime() / 1000 : System.currentTimeMillis() / 1000;
    }

    /**
     * Gibt an, ob das Programm gerade aus einer Jar heraus gestartet wurde.
     *
     * @return <code>true</code>, falls ja, sonst <code>false</code>.
     */
    @API
    public static boolean isJar() {
        String className = EngineAlpha.class.getName().replace('.', '/');
        String classJar = EngineAlpha.class.getResource("/" + className + ".class").toString();

        return classJar.startsWith("jar:");
    }

    /**
     * Gibt den Namen der Jar-Datei zurück, die gerade ausgeführt wird.
     *
     * @return Dateiname der Jar-Datei oder <code>null</code>, falls das Programm nicht über eine
     * Jar-Datei ausgeführt wird.
     */
    @API
    public static String getJarName() {
        String className = EngineAlpha.class.getName().replace('.', '/');
        String classJar = EngineAlpha.class.getResource("/" + className + ".class").toString();

        if (classJar.startsWith("jar:")) {
            String[] values = classJar.split("/");

            for (String value : values) {
                if (value.contains("!")) {
                    try {
                        return java.net.URLDecoder.decode(value.substring(0, value.length() - 1), StandardCharsets.UTF_8);
                    } catch (Exception e) {
                        return null;
                    }
                }
            }
        }

        return null;
    }

    /**
     * Gibt an, wann die Jar-Datei erzeugt wurde.
     *
     * @return Erzeugungsdatum der Jar-Datei in Sekunden seit dem 01.01.1970 (Unix Timestamp) oder
     * den aktuellen Timestamp, falls nicht von einer Jar-Datei ausgeführt.
     */
    @API
    public static long getBuildTime() {
        try {
            String uri = EngineAlpha.class.getName().replace('.', '/') + ".class";
            JarURLConnection j = (JarURLConnection) ClassLoader.getSystemResource(uri).openConnection();

            long time = j.getJarFile().getEntry("META-INF/MANIFEST.MF").getTime();
            return time > 0 ? time : System.currentTimeMillis() / 1000;
        } catch (Exception e) {
            return System.currentTimeMillis() / 1000;
        }
    }
}