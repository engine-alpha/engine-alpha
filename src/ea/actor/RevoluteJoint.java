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

import ea.internal.annotations.API;

public final class RevoluteJoint extends Joint<org.jbox2d.dynamics.joints.RevoluteJoint> {
    private float lowerLimit;
    private float upperLimit;

    /**
     * Geschwindigkeit in Umdrehungen / Sekunde
     */
    private float motorSpeed;
    private float maxMotorForce;
    private boolean motorEnabled;
    private boolean limitEnabled;

    protected void updateJoint(org.jbox2d.dynamics.joints.RevoluteJoint joint) {
        joint.setMaxMotorTorque(maxMotorForce); // same name as in PrismaticJoint
        joint.setLimits(lowerLimit, upperLimit);
        joint.setMotorSpeed((float) Math.toRadians(360 * motorSpeed));
        joint.enableMotor(motorEnabled);
        joint.enableLimit(limitEnabled);
    }

    @API
    public void setMaxMotorForce(float maxMotorForce) {
        this.maxMotorForce = maxMotorForce;
        this.motorEnabled = true;
        this.update();
    }

    @API
    public float getMaxMotorForce() {
        return maxMotorForce;
    }

    @API
    public float getLowerLimit() {
        return lowerLimit;
    }

    @API
    public void setLowerLimit(float lowerLimit) {
        this.lowerLimit = lowerLimit;
        this.limitEnabled = true;
        this.update();
    }

    @API
    public float getUpperLimit() {
        return upperLimit;
    }

    @API
    public void setUpperLimit(float upperLimit) {
        this.upperLimit = upperLimit;
        this.limitEnabled = true;
        this.update();
    }

    @API
    public float getMotorSpeed() {
        return motorSpeed;
    }

    @API
    public void setMotorSpeed(float motorSpeed) {
        this.motorSpeed = motorSpeed;
        this.motorEnabled = true;
        this.update();
    }

    @API
    public boolean isMotorEnabled() {
        return motorEnabled;
    }

    @API
    public void setMotorEnabled(boolean motorEnabled) {
        this.motorEnabled = motorEnabled;
        this.update();
    }

    @API
    public boolean isLimitEnabled() {
        return limitEnabled;
    }

    @API
    public void setLimitEnabled(boolean limitEnabled) {
        this.limitEnabled = limitEnabled;
        this.update();
    }
}
