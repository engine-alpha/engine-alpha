/*
 * Engine Alpha ist eine anfängerorientierte 2D-Gaming Engine.
 *
 * Copyright (c) 2011 - 2020 Michael Andonie and contributors.
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

import ea.internal.annotations.API;

public final class RevoluteJoint extends Joint<org.jbox2d.dynamics.joints.RevoluteJoint> {
    private float lowerLimit;
    private float upperLimit;

    private boolean motorEnabled;
    private boolean limitEnabled;

    /**
     * Geschwindigkeit in Umdrehungen / Sekunde
     */
    private float motorSpeed;
    private float maximumMotorTorque;

    @API
    public void setMaximumMotorTorque(float maximumMotorTorque) {
        this.maximumMotorTorque = maximumMotorTorque;
        this.motorEnabled = true;

        org.jbox2d.dynamics.joints.RevoluteJoint joint = getJoint();
        if (joint != null) {
            joint.setMaxMotorTorque(maximumMotorTorque);
            joint.enableMotor(true);
        }
    }

    @API
    public float getMaximumMotorTorque() {
        return maximumMotorTorque;
    }

    @API
    public float getLowerLimit() {
        return lowerLimit;
    }

    @API
    public void setLowerLimit(float lowerLimit) {
        this.lowerLimit = lowerLimit;
        this.limitEnabled = true;

        org.jbox2d.dynamics.joints.RevoluteJoint joint = getJoint();
        if (joint != null) {
            joint.setLimits(lowerLimit, upperLimit);
            joint.enableLimit(true);
        }
    }

    @API
    public float getUpperLimit() {
        return upperLimit;
    }

    @API
    public void setUpperLimit(float upperLimit) {
        this.upperLimit = upperLimit;
        this.limitEnabled = true;

        org.jbox2d.dynamics.joints.RevoluteJoint joint = getJoint();
        if (joint != null) {
            joint.setLimits(lowerLimit, upperLimit);
            joint.enableLimit(true);
        }
    }

    @API
    public void setLimits(float lowerLimit, float upperLimit) {
        this.lowerLimit = lowerLimit;
        this.upperLimit = upperLimit;
        this.limitEnabled = true;

        org.jbox2d.dynamics.joints.RevoluteJoint joint = getJoint();
        if (joint != null) {
            joint.setLimits(lowerLimit, upperLimit);
            joint.enableLimit(true);
        }
    }

    @API
    public float getMotorSpeed() {
        org.jbox2d.dynamics.joints.RevoluteJoint joint = getJoint();
        if (joint != null) {
            return (float) Math.toDegrees(joint.getMotorSpeed()) / 360;
        }

        return motorSpeed;
    }

    @API
    public void setMotorSpeed(float motorSpeed) {
        this.motorSpeed = motorSpeed;
        this.motorEnabled = true;

        org.jbox2d.dynamics.joints.RevoluteJoint joint = getJoint();
        if (joint != null) {
            float speed = (float) Math.toRadians(motorSpeed * 360);
            joint.setMotorSpeed(speed);
            joint.enableMotor(true);
        }
    }

    @API
    public boolean isMotorEnabled() {
        return motorEnabled;
    }

    @API
    public void setMotorEnabled(boolean motorEnabled) {
        this.motorEnabled = motorEnabled;

        org.jbox2d.dynamics.joints.RevoluteJoint joint = getJoint();
        if (joint != null) {
            joint.enableMotor(motorEnabled);
        }
    }

    @API
    public boolean isLimitEnabled() {
        return limitEnabled;
    }

    @API
    public void setLimitEnabled(boolean limitEnabled) {
        this.limitEnabled = limitEnabled;

        org.jbox2d.dynamics.joints.RevoluteJoint joint = getJoint();
        if (joint != null) {
            joint.enableMotor(limitEnabled);
        }
    }

    @Override
    protected void updateCustomProperties(org.jbox2d.dynamics.joints.RevoluteJoint joint) {
        joint.setMotorSpeed((float) Math.toRadians(motorSpeed * 360));
        joint.setMaxMotorTorque(maximumMotorTorque);
        joint.setLimits(lowerLimit, upperLimit);
        joint.enableLimit(limitEnabled);
        joint.enableMotor(motorEnabled);
    }
}
