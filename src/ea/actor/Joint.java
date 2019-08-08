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

package ea.actor;

import ea.event.EventListeners;
import ea.internal.annotations.API;
import ea.internal.annotations.Internal;
import ea.internal.physics.WorldHandler;
import javafx.util.Pair;

@API
public abstract class Joint<JointType extends org.jbox2d.dynamics.joints.Joint> {
    private Pair<JointType, WorldHandler> joint;
    private final EventListeners<Runnable> releaseListeners = new EventListeners<>();

    /**
     * A joint might be removed and recreated, so we allow to set it here. If it is recreated, the old one has been
     * automatically destroyed by the body destruction.
     */
    @Internal
    public final void setJoint(JointType joint, WorldHandler worldHandler) {
        if (joint != null) {
            updateJoint(joint);
        }

        this.joint = new Pair<>(joint, worldHandler);
    }

    @Internal
    abstract protected void updateJoint(JointType joint);

    @Internal
    protected void update() {
        if (this.joint != null) {
            updateJoint(this.joint.getKey());
        }
    }

    @API
    public void release() {
        if (joint != null) {
            joint.getValue().getWorld().destroyJoint(joint.getKey());
            joint = null;
        }

        releaseListeners.invoke(Runnable::run);
        releaseListeners.clear();
    }

    @API
    public void addReleaseListener(Runnable runnable) {
        releaseListeners.add(runnable);
    }
}
