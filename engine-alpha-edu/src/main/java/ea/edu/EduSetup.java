/*
 * Engine Alpha ist eine anfängerorientierte 2D-Gaming Engine.
 *
 * Copyright (c) 2011 - 2019 Michael Andonie and contributors.
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

package ea.edu;

import ea.Layer;
import ea.actor.Actor;
import ea.edu.event.BildAktualisierungReagierbar;
import ea.edu.event.MausKlickReagierbar;
import ea.edu.event.MausRadReagierbar;
import ea.edu.event.TastenReagierbar;
import ea.edu.internal.EduScene;
import ea.internal.annotations.API;
import ea.internal.annotations.Internal;

import java.util.function.Supplier;

public final class EduSetup {

    private static ThreadLocal<EduScene> customSetup = ThreadLocal.withInitial(() -> null);

    @Internal
    static <T extends Actor> void setup(EduActor<T> eduActor, EduScene eduScene) {
        Layer activeLayer = eduScene.getActiveLayer();

        activeLayer.defer(() -> activeLayer.add(eduActor.getActor()));

        if (eduActor instanceof BildAktualisierungReagierbar) {
            eduScene.addEduFrameUpdateListener((BildAktualisierungReagierbar) eduActor);
        }
        if (eduActor instanceof MausKlickReagierbar) {
            eduScene.addEduClickListener((MausKlickReagierbar) eduActor);
        }
        if (eduActor instanceof MausRadReagierbar) {
            eduScene.addEduMouseWheelListener((MausRadReagierbar) eduActor);
        }
        if (eduActor instanceof TastenReagierbar) {
            eduScene.addEduKeyListener((TastenReagierbar) eduActor);
        }
    }

    public static EduScene getActiveScene() {
        EduScene activeScene = customSetup.get();
        if (activeScene == null) {
            activeScene = Spiel.getActiveScene();
        }

        return activeScene;
    }

    /**
     * Erlaubt das Überspringen des automatischen EDU-Setups.
     *
     * @param runnable Code, der ohne automatischen Setup ausgeführt wird.
     * @param scene    Szene, zu der das neue Objekt hinzugefügt wird.
     */
    @API
    public static <T> T customSetup(Supplier<T> runnable, EduScene scene) {
        EduScene pre = customSetup.get();

        try {
            customSetup.set(scene);
            return runnable.get();
        } finally {
            customSetup.set(pre);
        }
    }

    private EduSetup() {
        // no objects should be created
    }
}
