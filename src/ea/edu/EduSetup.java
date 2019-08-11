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
import ea.edu.internal.EduScene;
import ea.internal.annotations.API;
import ea.internal.annotations.Internal;

import java.util.concurrent.atomic.AtomicBoolean;

public final class EduSetup {

    private static ThreadLocal<AtomicBoolean> skipSetup = ThreadLocal.withInitial(() -> new AtomicBoolean(false));

    @Internal
    static <T extends Actor> void setup(EduActor<T> eduActor) {
        if (isSetupSkipped()) {
            return;
        }

        EduScene activeScene = Spiel.getActiveScene();
        Layer activeLayer = activeScene.getActiveLayer();

        activeLayer.defer(() -> activeLayer.add(eduActor.getActor()));
    }

    public static boolean isSetupSkipped() {
        return skipSetup.get().get();
    }

    /**
     * Erlaubt das überspringen des automatischen EDU-Setups.
     *
     * @param runnable Code, der ohne automatischen Setup ausgeführt wird.
     */
    @API
    public void skipSetup(Runnable runnable) {
        AtomicBoolean skipSetup = EduSetup.skipSetup.get();
        boolean pre = skipSetup.get();

        runnable.run();

        skipSetup.set(pre);
    }

    private EduSetup() {
        // no objects should be created
    }
}
