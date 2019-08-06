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
import ea.internal.annotations.Internal;

import java.awt.Graphics2D;

/**
 * Abstrakte Klasse beschreibt eine Tile-Instanz für den Tile-Atlas. <i>In jeder Engine-Instanz existiert jedes Tile
 * nur einmal im Atlas.</i>
 */
@API
public interface Tile {
    @Internal
    void render(Graphics2D g, float width, float height);
}
