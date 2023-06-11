/*
 * Engine Alpha ist eine anfängerorientierte 2D-Gaming Engine.
 *
 * Copyright (c) 2011 - 2023 Michael Andonie and contributors.
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

package ea.actor;

import ea.event.EventListeners;
import ea.internal.annotations.API;
import ea.internal.annotations.Internal;
import ea.internal.physics.WorldHandler;

/**
 * Verbindung zwischen Objekten.
 *
 * @param <JointType> Typ der Verbindung in der Box2D-Repräsentation
 */
@API
public abstract class Joint<JointType extends org.jbox2d.dynamics.joints.Joint> {
    private JointRegistration<JointType> joint;
    private final EventListeners<Runnable> releaseListeners = new EventListeners<>();

    /**
     * A joint might be removed and recreated, so we allow to set it here. If it is recreated, the old one has been
     * automatically destroyed by the body destruction.
     */
    @Internal
    public final void setJoint(JointType joint, WorldHandler worldHandler) {
        this.joint = new JointRegistration<>(joint, worldHandler);

        updateCustomProperties(joint);
    }

    protected abstract void updateCustomProperties(JointType joint);

    @Internal
    protected final JointType getJoint() {
        JointRegistration<JointType> joint = this.joint;
        if (joint == null) {
            return null;
        }

        return joint.getJoint();
    }

    /**
     * Löst die Verbindung der Objekte.
     */
    @API
    public void release() {
        if (joint != null) {
            joint.getWorldHandler().getWorld().destroyJoint(joint.getJoint());
            joint = null;
        }

        releaseListeners.invoke(Runnable::run);
        releaseListeners.clear();
    }

    /**
     * Fügt einen Listener hinzu, der ausgeführt wird, sobald die Verbindung gelöst wird.
     *
     * @param runnable Listener
     */
    @API
    public void addReleaseListener(Runnable runnable) {
        releaseListeners.add(runnable);
    }

    public static class JointRegistration<T> {
        private final T joint;
        private final WorldHandler worldHandler;

        public JointRegistration(T joint, WorldHandler worldHandler) {
            this.joint = joint;
            this.worldHandler = worldHandler;
        }

        public T getJoint() {
            return joint;
        }

        public WorldHandler getWorldHandler() {
            return worldHandler;
        }
    }
}
