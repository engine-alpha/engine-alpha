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

public final class PrismaticJoint extends Joint<org.jbox2d.dynamics.joints.PrismaticJoint> {
    private float lowerLimit;
    private float upperLimit;

    private boolean motorEnabled;
    private boolean limitEnabled;

    /**
     * Geschwindigkeit in m / s
     */
    private float motorSpeed;
    private float maximumMotorForce;

    @API
    public void setMaximumMotorForce(float maximumMotorForce) {
        this.maximumMotorForce = maximumMotorForce;
        this.motorEnabled = true;

        org.jbox2d.dynamics.joints.PrismaticJoint joint = getJoint();
        if (joint != null) {
            joint.setMaxMotorForce(maximumMotorForce);
            joint.enableMotor(true);
        }
    }

    @API
    public float getMaximumMotorForce() {
        return maximumMotorForce;
    }

    @API
    public float getLowerLimit() {
        return lowerLimit;
    }

    @API
    public void setLowerLimit(float lowerLimit) {
        this.lowerLimit = lowerLimit;
        this.limitEnabled = true;

        org.jbox2d.dynamics.joints.PrismaticJoint joint = getJoint();
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

        org.jbox2d.dynamics.joints.PrismaticJoint joint = getJoint();
        if (joint != null) {
            joint.setLimits(lowerLimit, upperLimit);
            joint.enableLimit(true);
        }
    }

    @API
    public float getMotorSpeed() {
        org.jbox2d.dynamics.joints.PrismaticJoint joint = getJoint();
        if (joint != null) {
            return joint.getMotorSpeed();
        }

        return motorSpeed;
    }

    @API
    public void setMotorSpeed(float motorSpeed) {
        this.motorSpeed = motorSpeed;
        this.motorEnabled = true;

        org.jbox2d.dynamics.joints.PrismaticJoint joint = getJoint();
        if (joint != null) {
            joint.setMotorSpeed(motorSpeed);
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

        org.jbox2d.dynamics.joints.PrismaticJoint joint = getJoint();
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

        org.jbox2d.dynamics.joints.PrismaticJoint joint = getJoint();
        if (joint != null) {
            joint.enableLimit(limitEnabled);
        }
    }

    @API
    public void setLimits(float lower, float upper) {
        setLowerLimit(lower);
        setUpperLimit(upper);
    }

    @API
    public float getTranslation() {
        org.jbox2d.dynamics.joints.PrismaticJoint joint = getJoint();
        if (joint == null) {
            return 0;
        }

        return joint.getJointTranslation();
    }

    @Override
    protected void updateCustomProperties(org.jbox2d.dynamics.joints.PrismaticJoint joint) {
        joint.setMotorSpeed(motorSpeed);
        joint.setMaxMotorForce(maximumMotorForce);
        joint.setLimits(lowerLimit,upperLimit);
        joint.enableMotor(motorEnabled);
        joint.enableLimit(limitEnabled);
    }
}
