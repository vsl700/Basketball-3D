/**
 * 
 */
package com.vasciie.bkbl.themes;

import java.util.ArrayList;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Pixmap.Format;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.VertexAttributes;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.attributes.TextureAttribute;
import com.badlogic.gdx.graphics.g3d.model.Node;
import com.badlogic.gdx.graphics.g3d.utils.MeshPartBuilder;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;
import com.badlogic.gdx.graphics.g3d.utils.shapebuilders.BoxShapeBuilder;
import com.badlogic.gdx.graphics.g3d.utils.shapebuilders.CylinderShapeBuilder;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector3;
import com.vasciie.bkbl.gamespace.entities.EntityType;
import com.vasciie.bkbl.gamespace.entities.Player;
import com.vasciie.bkbl.gamespace.objects.Terrain;

/**
 * Themes for the terrain for each specific gamemode
 * 
 * @author studi
 *
 */
public enum TerrainThemes {

	
	EASY{

		@Override
		public void createModels(Terrain terrain) {
			float wallHeight = 15;
			float wallDepth = 0.1f, wallDepth2 = 0.05f;
			float wallSpace = 2.5f, wallSpace2 = 4;
			
			ModelBuilder mb = new ModelBuilder();
			//PLAYGROUND
			Material material = new Material(ColorAttribute.createDiffuse(Color.GRAY.cpy().add(0.3f, 0.3f, 0.3f, 0)));
			
			
			mb.begin();
			float temp;
			for (int i = 0; (temp = -terrain.getWidth() / 2 + i * wallSpace) <= terrain.getWidth() / 2; i++) {
				Node wall = mb.node();
				wall.id = "fenceWall1." + i;
				wall.translation.set(temp, wallHeight / 2, terrain.getDepth() / 2);
				BoxShapeBuilder.build(mb.part(wall.id, GL20.GL_TRIANGLES, VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal, material), wallDepth, wallHeight, wallDepth);
			}
			
			for (int i = 1; (temp = wallHeight - i * wallSpace2) >= 0; i++) {
				Node wall = mb.node();
				wall.id = "fenceWallH1." + i;
				wall.translation.set(0, temp, terrain.getDepth() / 2);
				BoxShapeBuilder.build(mb.part(wall.id, GL20.GL_TRIANGLES, VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal, material), terrain.getWidth(), wallDepth2, wallDepth2);
			}
			
			Node wallUpper = mb.node();
			wallUpper.id = "fenceWall1.Upper";
			wallUpper.translation.set(0, wallHeight, terrain.getDepth() / 2);
			BoxShapeBuilder.build(mb.part(wallUpper.id, GL20.GL_TRIANGLES, VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal, material), terrain.getWidth(), wallDepth, wallDepth);
			
			
			for (int i = 0; (temp = -terrain.getDepth() / 2 + i * wallSpace) <= terrain.getDepth() / 2; i++) {
				Node wall = mb.node();
				wall.id = "fenceWall2." + i;
				wall.translation.set(terrain.getWidth() / 2, wallHeight / 2, temp);
				BoxShapeBuilder.build(mb.part(wall.id, GL20.GL_TRIANGLES, VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal, material), wallDepth, wallHeight, wallDepth);
			}
			
			for (int i = 1; (temp = wallHeight - i * wallSpace2) >= 0; i++) {
				Node wall = mb.node();
				wall.id = "fenceWallH2." + i;
				wall.translation.set(terrain.getWidth() / 2, temp, 0);
				BoxShapeBuilder.build(mb.part(wall.id, GL20.GL_TRIANGLES, VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal, material), wallDepth2, wallDepth2, terrain.getDepth());
			}
			
			Node wallUpper2 = mb.node();
			wallUpper2.id = "fenceWall2.Upper";
			wallUpper2.translation.set(terrain.getWidth() / 2, wallHeight, 0);
			BoxShapeBuilder.build(mb.part(wallUpper2.id, GL20.GL_TRIANGLES, VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal, material), wallDepth, wallDepth, terrain.getDepth());
			
			
			for (int i = 0; (temp = -terrain.getWidth() / 2 + i * wallSpace) <= terrain.getWidth() / 2; i++) {
				Node wall = mb.node();
				wall.id = "fenceWall3." + i;
				wall.translation.set(temp, wallHeight / 2, -terrain.getDepth() / 2);
				BoxShapeBuilder.build(mb.part(wall.id, GL20.GL_TRIANGLES, VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal, material), wallDepth, wallHeight, wallDepth);
			}
			
			for (int i = 1; (temp = wallHeight - i * wallSpace2) >= 0; i++) {
				Node wall = mb.node();
				wall.id = "fenceWallH3." + i;
				wall.translation.set(0, temp, -terrain.getDepth() / 2);
				BoxShapeBuilder.build(mb.part(wall.id, GL20.GL_TRIANGLES, VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal, material), terrain.getWidth(), wallDepth2, wallDepth2);
			}
			
			Node wallUpper3 = mb.node();
			wallUpper3.id = "fenceWall3.Upper";
			wallUpper3.translation.set(0, wallHeight, -terrain.getDepth() / 2);
			BoxShapeBuilder.build(mb.part(wallUpper3.id, GL20.GL_TRIANGLES, VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal, material), terrain.getWidth(), wallDepth, wallDepth);
			
			
			for (int i = 0; (temp = -terrain.getDepth() / 2 + i * wallSpace) <= terrain.getDepth() / 2; i++) {
				Node wall = mb.node();
				wall.id = "fenceWall4." + i;
				wall.translation.set(-terrain.getWidth() / 2, wallHeight / 2, temp);
				BoxShapeBuilder.build(mb.part(wall.id, GL20.GL_TRIANGLES, VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal, material), wallDepth, wallHeight, wallDepth);
			}
			
			for (int i = 1; (temp = wallHeight - i * wallSpace2) >= 0; i++) {
				Node wall = mb.node();
				wall.id = "fenceWallH4." + i;
				wall.translation.set(-terrain.getWidth() / 2, temp, 0);
				BoxShapeBuilder.build(mb.part(wall.id, GL20.GL_TRIANGLES, VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal, material), wallDepth2, wallDepth2, terrain.getDepth());
			}
			
			Node wallUpper4 = mb.node();
			wallUpper4.id = "fenceWall4.Upper";
			wallUpper4.translation.set(-terrain.getWidth() / 2, wallHeight, 0);
			BoxShapeBuilder.build(mb.part(wallUpper4.id, GL20.GL_TRIANGLES, VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal, material), wallDepth, wallDepth, terrain.getDepth());
			
			
			Node cityPlate = mb.node();
			cityPlate.id = "cityPlate";
			cityPlate.translation.set(0, -0.1f, 0);
			BoxShapeBuilder.build(mb.part(cityPlate.id, GL20.GL_TRIANGLES, VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal, material), terrain.getWidth() * 8, 0.01f, terrain.getDepth() * 8);
			
			//STREETS
			float streetWidth = 11f, streetMarkWidth = 1f, streetMarkDepth = 4.5f, streetMarkSpace = 3, streetYPos = cityPlate.translation.y + 0.011f, streetHeight = 0.3f;
			
			Material materialStreet = new Material(ColorAttribute.createDiffuse(Color.GRAY));
			Material materialStreetMark = new Material(ColorAttribute.createDiffuse(Color.WHITE));
			
			Node street1 = mb.node();
			street1.translation.set(terrain.getWidth() + streetWidth / 2, streetYPos, 0);
			BoxShapeBuilder.build(mb.part(street1.id, GL20.GL_TRIANGLES, VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal, materialStreet), streetWidth, 0.01f, terrain.getDepth() * 8 + streetWidth * 2);
			
			for(int i = 0; (temp = -terrain.getDepth() + streetMarkSpace + i * (streetMarkDepth + streetMarkSpace)) <= terrain.getDepth() - streetMarkSpace; i++) {
				Node streetMark = mb.node();
				streetMark.translation.set(terrain.getWidth() + streetWidth / 2, streetYPos, temp);
				BoxShapeBuilder.build(mb.part(streetMark.id, GL20.GL_TRIANGLES, VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal, materialStreetMark), streetMarkWidth, streetHeight, streetMarkDepth);
			
				Node streetMark1 = mb.node();
				streetMark1.translation.set(-(terrain.getWidth() + streetWidth / 2), streetYPos, temp);
				BoxShapeBuilder.build(mb.part(streetMark1.id, GL20.GL_TRIANGLES, VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal, materialStreetMark), streetMarkWidth, streetHeight, streetMarkDepth);
			}
			
			Node street2 = mb.node();
			street2.translation.set(0, streetYPos, terrain.getDepth() + streetWidth / 2);
			BoxShapeBuilder.build(mb.part(street2.id, GL20.GL_TRIANGLES, VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal, materialStreet), terrain.getWidth() * 8 + streetWidth * 2, 0.01f, streetWidth);
			
			for(int i = 0; (temp = -terrain.getWidth() + streetMarkSpace + i * (streetMarkDepth + streetMarkSpace)) <= terrain.getWidth() - streetMarkSpace; i++) {
				Node streetMark = mb.node();
				streetMark.translation.set(temp, streetYPos, terrain.getDepth() + streetWidth / 2);
				BoxShapeBuilder.build(mb.part(streetMark.id, GL20.GL_TRIANGLES, VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal, materialStreetMark), streetMarkDepth, streetHeight, streetMarkWidth);
				
				Node streetMark1 = mb.node();
				streetMark1.translation.set(temp, streetYPos, -(terrain.getDepth() + streetWidth / 2));
				BoxShapeBuilder.build(mb.part(streetMark1.id, GL20.GL_TRIANGLES, VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal, materialStreetMark), streetMarkDepth, streetHeight, streetMarkWidth);
			}
			
			Node street3 = mb.node();
			street3.translation.set(-(terrain.getWidth() + streetWidth / 2), streetYPos, 0);
			BoxShapeBuilder.build(mb.part(street3.id, GL20.GL_TRIANGLES, VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal, materialStreet), streetWidth, 0.01f, terrain.getDepth() * 8 + streetWidth * 2);
			
			Node street4 = mb.node();
			street4.translation.set(0, streetYPos, -(terrain.getDepth() + streetWidth / 2));
			BoxShapeBuilder.build(mb.part(street4.id, GL20.GL_TRIANGLES, VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal, materialStreet), terrain.getWidth() * 8 + streetWidth * 2, 0.01f, streetWidth);
			
			
			for(int i = 0; (temp = terrain.getDepth() + streetWidth + streetMarkSpace + i * (streetMarkDepth + streetMarkSpace)) <= terrain.getDepth() * 4 - streetMarkSpace; i++) {
				Node streetMark = mb.node();
				streetMark.translation.set(terrain.getWidth() + streetWidth / 2, streetYPos, temp);
				BoxShapeBuilder.build(mb.part(streetMark.id, GL20.GL_TRIANGLES, VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal, materialStreetMark), streetMarkWidth, streetHeight, streetMarkDepth);
				
				Node streetMark1 = mb.node();
				streetMark1.translation.set(-(terrain.getWidth() + streetWidth / 2), streetYPos, temp);
				BoxShapeBuilder.build(mb.part(streetMark1.id, GL20.GL_TRIANGLES, VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal, materialStreetMark), streetMarkWidth, streetHeight, streetMarkDepth);
			}
			
			for(int i = 0; (temp = terrain.getWidth() + streetWidth + streetMarkSpace + i * (streetMarkDepth + streetMarkSpace)) <= terrain.getWidth() * 4 - streetMarkSpace; i++) {
				Node streetMark = mb.node();
				streetMark.translation.set(temp, streetYPos, terrain.getDepth() + streetWidth / 2);
				BoxShapeBuilder.build(mb.part(streetMark.id, GL20.GL_TRIANGLES, VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal, materialStreetMark), streetMarkDepth, streetHeight, streetMarkWidth);
				
				Node streetMark1 = mb.node();
				streetMark1.translation.set(temp, streetYPos, -(terrain.getDepth() + streetWidth / 2));
				BoxShapeBuilder.build(mb.part(streetMark1.id, GL20.GL_TRIANGLES, VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal, materialStreetMark), streetMarkDepth, streetHeight, streetMarkWidth);
			}
			
			for(int i = 0; (temp = -(terrain.getDepth() + streetWidth + streetMarkSpace + i * (streetMarkDepth + streetMarkSpace))) >= -terrain.getDepth() * 4 - streetMarkSpace; i++) {
				Node streetMark = mb.node();
				streetMark.translation.set(terrain.getWidth() + streetWidth / 2, streetYPos, temp);
				BoxShapeBuilder.build(mb.part(streetMark.id, GL20.GL_TRIANGLES, VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal, materialStreetMark), streetMarkWidth, streetHeight, streetMarkDepth);
				
				Node streetMark1 = mb.node();
				streetMark1.translation.set(-(terrain.getWidth() + streetWidth / 2), streetYPos, temp);
				BoxShapeBuilder.build(mb.part(streetMark1.id, GL20.GL_TRIANGLES, VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal, materialStreetMark), streetMarkWidth, streetHeight, streetMarkDepth);
			}
			
			for(int i = 0; (temp = -(terrain.getWidth() + streetWidth + streetMarkSpace + i * (streetMarkDepth + streetMarkSpace))) >= -terrain.getWidth() * 4 - streetMarkSpace; i++) {
				Node streetMark = mb.node();
				streetMark.translation.set(temp, streetYPos, terrain.getDepth() + streetWidth / 2);
				BoxShapeBuilder.build(mb.part(streetMark.id, GL20.GL_TRIANGLES, VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal, materialStreetMark), streetMarkDepth, streetHeight, streetMarkWidth);
				
				Node streetMark1 = mb.node();
				streetMark1.translation.set(temp, streetYPos, -(terrain.getDepth() + streetWidth / 2));
				BoxShapeBuilder.build(mb.part(streetMark1.id, GL20.GL_TRIANGLES, VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal, materialStreetMark), streetMarkDepth, streetHeight, streetMarkWidth);
			}
			
			modelInstances.add(new ModelInstance(mb.end()));
			
			
			//BUILDINGS
			Material[] buildingMaterials = new Material[] {
					new Material(ColorAttribute.createDiffuse(Color.PINK)),
					new Material(ColorAttribute.createDiffuse(Color.CYAN)),
					new Material(ColorAttribute.createDiffuse(Color.RED)),
					new Material(ColorAttribute.createDiffuse(Color.ORANGE)),
					new Material(ColorAttribute.createDiffuse(Color.BLUE))
			};
			
			Material windowMaterial = new Material(ColorAttribute.createDiffuse(Color.YELLOW));
			
			ModelBuilder childMB = new ModelBuilder();
			
			//mb.begin();
			//childMB.begin();
			
			createBuildingsGroup(-terrain.getWidth() + 8, 80, 8, 8, 6, 4, buildingMaterials, windowMaterial, mb, childMB);
			createBuildingsGroup(terrain.getWidth() + 19, 80, 8, 8, 4, 4, buildingMaterials, windowMaterial, mb, childMB);
			createBuildingsGroup(-terrain.getWidth()*2 - 34 + 17, 80, 8, 8, 4, 4, buildingMaterials, windowMaterial, mb, childMB);
			
			createBuildingsGroup(48, -terrain.getDepth() - 117 + 53 + 18, 8, 8, 4, 4, buildingMaterials, windowMaterial, mb, childMB);
			createBuildingsGroup(48, -terrain.getDepth() + 11, 8, 8, 4, 12, buildingMaterials, windowMaterial, mb, childMB);
			
			createBuildingsGroup(-(terrain.getWidth() - 8), -107, 8, 8, 6, 4, buildingMaterials, windowMaterial, mb, childMB);
			createBuildingsGroup(-(terrain.getWidth()*2 + 34 - 18), -125 + 18, 8, 8, 4, 4, buildingMaterials, windowMaterial, mb, childMB);
			
			createBuildingsGroup(-76, -terrain.getDepth() + 11, 8, 8, 4, 12, buildingMaterials, windowMaterial, mb, childMB);
			
			//childMB.end();
			
			//model = mb.end();
			
			//NEW TERRAIN
			Texture court = new Texture(Gdx.files.internal("game/basketball_court_easy.jpg"));
			court.setWrap(Texture.TextureWrap.ClampToEdge, Texture.TextureWrap.ClampToEdge);
			court.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);

			Material materialCourt = new Material(TextureAttribute.createDiffuse(court));
			long attribs = VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal | VertexAttributes.Usage.TextureCoordinates;

			mb.begin();
			Node terrainNode = mb.node();
			terrainNode.translation.set(0, -0.001f, 0);
			BoxShapeBuilder.build(mb.part(terrainNode.id, GL20.GL_TRIANGLES, attribs, materialCourt), terrain.getWidth(), 0.001f, terrain.getDepth());
			customTerrainModel = mb.end();
			customTerrainModel.manageDisposable(court);
		}
		
		private void createBuildingsGroup(float x, float z, int w, int d, int timesX, int timesZ, Material[] materials, Material windowMaterial, ModelBuilder mb, ModelBuilder childMB) {
			int minSize = 2, maxSize = 6;
			float buildingSpace = 1;
			for(int i = 0; i < timesX; i++) {
				Model temp;
				
				if(i == timesX - 1)
					temp = createAngleBuilding(x + (buildingSpace + w) * i, 0, z, w, MathUtils.random(minSize, maxSize), d, -90, materials[MathUtils.random(materials.length - 1)], windowMaterial, mb, childMB);
				else if(i == 0) 
					temp = createAngleBuilding(x + (buildingSpace + w) * i, 0, z, w, MathUtils.random(minSize, maxSize), d, 0, materials[MathUtils.random(materials.length - 1)], windowMaterial, mb, childMB);
				else 
					temp = createBuilding(x + (buildingSpace + w) * i, 0, z, w, MathUtils.random(minSize, maxSize), d, 0, materials[MathUtils.random(materials.length - 1)], windowMaterial, mb, childMB);
				
				modelInstances.add(new ModelInstance(temp));
			}
			
			for(int i = 0; i < timesX; i++) {
				Model temp;
				
				if(i == timesX - 1)
					temp = createAngleBuilding(x + (buildingSpace + w) * i, 0, z + (d + buildingSpace) * (timesZ - 1), w, MathUtils.random(minSize, maxSize), d, 180, materials[MathUtils.random(materials.length - 1)], windowMaterial, mb, childMB);
				else if(i == 0)
					temp = createAngleBuilding(x + (buildingSpace + w) * i, 0, z + (d + buildingSpace) * (timesZ - 1), w, MathUtils.random(minSize, maxSize), d, 90, materials[MathUtils.random(materials.length - 1)], windowMaterial, mb, childMB);
				else 
					temp = createBuilding(x + (buildingSpace + w) * i, 0, z + (d + buildingSpace) * (timesZ - 1), w, MathUtils.random(minSize, maxSize), d, 180, materials[MathUtils.random(materials.length - 1)], windowMaterial, mb, childMB);
				
				modelInstances.add(new ModelInstance(temp));
			}
			
			for(int i = 1; i < timesZ - 1; i++) {
				Model temp;
				
				if(i == timesZ - 2 || i == 1)
					temp = createAngleBuilding(x, 0, z + (buildingSpace + d) * i, w, MathUtils.random(minSize, maxSize), d, 90, materials[MathUtils.random(materials.length - 1)], windowMaterial, mb, childMB);
				else 
					temp = createBuilding(x, 0, z + (buildingSpace + d) * i, w, MathUtils.random(minSize, maxSize), d, 90, materials[MathUtils.random(materials.length - 1)], windowMaterial, mb, childMB);
				
				modelInstances.add(new ModelInstance(temp));
			}
			
			for(int i = 1; i < timesZ - 1; i++) {
				Model temp;
				
				if(i == timesZ - 2 || i == 1)
					temp = createBuilding(x + (w + buildingSpace) * (timesX - 1), 0, z + (buildingSpace + d) * i, w, MathUtils.random(minSize, maxSize), d, -90, materials[MathUtils.random(materials.length - 1)], windowMaterial, mb, childMB);
				else 
					temp = createBuilding(x + (w + buildingSpace) * (timesX - 1), 0, z + (buildingSpace + d) * i, w, MathUtils.random(minSize, maxSize), d, -90, materials[MathUtils.random(materials.length - 1)], windowMaterial, mb, childMB);
				
				modelInstances.add(new ModelInstance(temp));
			}
		}
		
		private Model createBuilding(float x, float y, float z, int w, int h, float d, float rotation, Material material, Material windowMaterial, ModelBuilder mb, ModelBuilder childMB) {
			mb.begin();
			childMB.begin();
			
			float buildingHeight = 6;
			
			for (int i = 0; i < h; i++) {
				float realY = (y + buildingHeight / 2) * (i + 1);
				
				Node building = mb.node();
				building.translation.set(x, realY, z);
				building.rotation.setEulerAngles(rotation, 0, 0);
				BoxShapeBuilder.build(mb.part(building.id, GL20.GL_TRIANGLES, VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal, material), w, buildingHeight, d);
				
				float windowSize = buildingHeight / 6, windowSpace = 3, windowDepth = 0.1f;
				float temp;
				for (int j = 0; (temp = -windowSize - windowSpace + (j + 1) * (windowSpace / 2 + windowSize / 2)) <= w - windowSize * 1.5f - windowSpace; j++) {
					Node window = childMB.node();
					window.translation.set(temp, 0, -d / 2 - windowDepth * 3);
					BoxShapeBuilder.build(childMB.part(window.id, GL20.GL_TRIANGLES, VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal, windowMaterial), windowSize, windowSize, windowDepth);
					building.addChild(window);
				}
			}
			
			childMB.end();
			
			return mb.end();
		}
		
		private Model createAngleBuilding(float x, float y, float z, int w, int h, float d, float rotation, Material material, Material windowMaterial, ModelBuilder mb, ModelBuilder childMB) {
			mb.begin();
			childMB.begin();
			
			float buildingHeight = 6;
			
			for (int i = 0; i < h; i++) {
				float realY = (y + buildingHeight / 2) * (i + 1);
				
				Node building = mb.node();
				building.translation.set(x, realY, z);
				building.rotation.setEulerAngles(rotation, 0, 0);
				BoxShapeBuilder.build(mb.part(building.id, GL20.GL_TRIANGLES, VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal, material), w, buildingHeight, d);
				
				float windowSize = buildingHeight / 6, windowSpace = 3, windowDepth = 0.1f;
				float temp;
				for (int j = 0; (temp = -windowSize - windowSpace + (j + 1) * (windowSpace / 2 + windowSize / 2)) <= w - windowSize * 1.5f - windowSpace; j++) {
					Node window = childMB.node();
					window.translation.set(temp, 0, -d / 2 - windowDepth * 3);
					BoxShapeBuilder.build(childMB.part(window.id, GL20.GL_TRIANGLES, VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal, windowMaterial), windowSize, windowSize, windowDepth);
					building.addChild(window);
				}
				
				for (int j = 0; (temp = -windowSize - windowSpace + (j + 1) * (windowSpace / 2 + windowSize / 2)) <= d - windowSize * 1.5f - windowSpace; j++) {
					Node window = childMB.node();
					window.translation.set(-w / 2 - windowDepth * 2, 0, temp);
					BoxShapeBuilder.build(childMB.part(window.id, GL20.GL_TRIANGLES, VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal, windowMaterial), windowDepth, windowSize, windowSize);
					building.addChild(window);
				}
			}
			
			childMB.end();
			
			return mb.end();
		}

		@Override
		public boolean hasOwnTerrain() {
			
			return true;
		}

		@Override
		public Color getThemeColor() {
			
			return //new Color(0.6f, 0.5f, 0, 1);
					null;
		}
		
	},
	
	
	HARD{

		@Override
		public void createModels(Terrain terrain) {
			Pixmap pm = new Pixmap(64, 64, Format.RGBA8888);
			pm.setColor(Color.BROWN);
			pm.fillRectangle(0, 0, pm.getWidth(), pm.getHeight());
			pm.setColor(Color.BROWN.cpy().add(0.1f, 0.1f, 0, 0));
			pm.fillCircle(pm.getWidth() / 2, pm.getWidth() / 2, pm.getWidth() / 2);
			
			Texture texture = new Texture(pm);
			pm.dispose();
			
			ModelBuilder mb = new ModelBuilder();
			
			Material wallsMaterial = new Material(TextureAttribute.createDiffuse(texture));
			texture.setWrap(Texture.TextureWrap.Repeat, Texture.TextureWrap.Repeat);
			texture.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
			
			Material wallsMaterial1 = new Material(ColorAttribute.createDiffuse(Color.GOLD));
			
			Material ladderMaterial = new Material(ColorAttribute.createDiffuse(Color.ORANGE));
			
			Material material = new Material(ColorAttribute.createDiffuse(Color.GRAY.cpy().add(0.3f, 0.3f, 0.3f, 0)));
			
			
			float wallHeight = 20, wallDepth = 0.01f;
			
			mb.begin();
			
			Node wallN = mb.node();
			wallN.translation.set(0, wallHeight / 2, terrain.getDepth() / 2);
			BoxShapeBuilder.build(part(mb.part(wallN.id, GL20.GL_TRIANGLES, VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal | VertexAttributes.Usage.TextureCoordinates, wallsMaterial), terrain.getWidth(), wallHeight), terrain.getWidth(), wallHeight, wallDepth);
			
			Node wallS = mb.node();
			wallS.translation.set(0, wallHeight / 2, -terrain.getDepth() / 2);
			BoxShapeBuilder.build(part(mb.part(wallS.id, GL20.GL_TRIANGLES, VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal | VertexAttributes.Usage.TextureCoordinates, wallsMaterial), terrain.getWidth(), wallHeight), terrain.getWidth(), wallHeight, wallDepth);
			
			Node wallW = mb.node();
			wallW.translation.set(-terrain.getWidth() / 2, wallHeight / 2, 0);
			BoxShapeBuilder.build(part(mb.part(wallW.id, GL20.GL_TRIANGLES, VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal | VertexAttributes.Usage.TextureCoordinates, wallsMaterial), terrain.getDepth(), wallHeight), wallDepth, wallHeight, terrain.getDepth());
			
			float bottomPart = 0.15f, windowPart = 0.5f, upperPart = 1 - bottomPart - windowPart;
			float wallHeight1 = wallHeight * bottomPart, wallHeight2 = wallHeight * upperPart, wallHeight3 = wallHeight * windowPart;
			
			Node wallE1 = mb.node();
			wallE1.translation.set(terrain.getWidth() / 2, wallHeight1 / 2, 0);
			BoxShapeBuilder.build(mb.part(wallE1.id, GL20.GL_TRIANGLES, VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal, wallsMaterial1), wallDepth, wallHeight1, terrain.getDepth());
			
			Node wallE2 = mb.node();
			wallE2.translation.set(terrain.getWidth() / 2, wallHeight - wallHeight2 / 2, 0);
			BoxShapeBuilder.build(mb.part(wallE2.id, GL20.GL_TRIANGLES, VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal, wallsMaterial1), wallDepth, wallHeight2, terrain.getDepth());
			
			Node wallE3 = mb.node();
			wallE3.translation.set(terrain.getWidth() / 2, wallHeight1 + wallHeight3 / 2, -terrain.getDepth() / 4);
			BoxShapeBuilder.build(mb.part(wallE3.id, GL20.GL_TRIANGLES, VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal, material), wallDepth, wallHeight3, 1);
			
			Node wallE4 = mb.node();
			wallE4.translation.set(terrain.getWidth() / 2, wallHeight1 + wallHeight3 / 2, terrain.getDepth() / 4);
			BoxShapeBuilder.build(mb.part(wallE4.id, GL20.GL_TRIANGLES, VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal, material), wallDepth, wallHeight3, 1);
			
			Node wallE5 = mb.node();
			wallE5.translation.set(terrain.getWidth() / 2, wallHeight1 + wallHeight3 / 2, 0);
			BoxShapeBuilder.build(mb.part(wallE5.id, GL20.GL_TRIANGLES, VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal, material), wallDepth, wallHeight3, 1);
			
			Node ceiling = mb.node();
			ceiling.translation.set(0, wallHeight, 0);
			BoxShapeBuilder.build(mb.part(ceiling.id, GL20.GL_TRIANGLES, VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal, material), terrain.getWidth(), wallDepth, terrain.getDepth());
			
			float worldPlateWidth = terrain.getWidth() * 4;
			Node worldPlate = mb.node();
			worldPlate.id = "worldPlate";
			worldPlate.translation.set(worldPlateWidth / 2 - terrain.getWidth() / 2, -0.1f, 0);
			BoxShapeBuilder.build(mb.part(worldPlate.id, GL20.GL_TRIANGLES, VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal, material), worldPlateWidth, 0.01f, terrain.getDepth() * 4);
			
			modelInstances.add(new ModelInstance(mb.end()));
			
			
			int divisions = 15;
			float ladderRadius = 0.5f, ladderHeight = wallHeight - 1, ladderWidth = 10;
			float startLadderZ = terrain.getDepth() / 3;
			
			mb.begin();
			Node ladder1 = mb.node();
			ladder1.translation.set(-terrain.getWidth() / 2 + ladderRadius, ladderHeight / 2, startLadderZ);
			CylinderShapeBuilder.build(mb.part(ladder1.id, GL20.GL_TRIANGLES, VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal, ladderMaterial), ladderRadius, ladderHeight, ladderRadius, divisions);
			
			Node ladder2 = mb.node();
			ladder2.translation.set(-terrain.getWidth() / 2 + ladderRadius, ladderHeight / 2, startLadderZ - ladderWidth);
			CylinderShapeBuilder.build(mb.part(ladder2.id, GL20.GL_TRIANGLES, VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal, ladderMaterial), ladderRadius, ladderHeight, ladderRadius, divisions);
			
			for (int m = 0; m < 3; m++) {
				for (int i = 1; i <= 5 - m; i++) {
					Node ladder3 = mb.node();
					ladder3.translation.set(-terrain.getWidth() / 2 + ladderRadius, i + 6 * m, startLadderZ - ladderWidth / 2);
					ladder3.rotation.setEulerAngles(0, 90, 0);
					CylinderShapeBuilder.build(mb.part(ladder3.id, GL20.GL_TRIANGLES, VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal, ladderMaterial), ladderRadius, ladderWidth, ladderRadius, divisions);
				}
			}
			
			Model tempModel = mb.end();
			
			modelInstances.add(new ModelInstance(tempModel));
			modelInstances.add(new ModelInstance(tempModel, 0, 0, -ladderWidth / 2 - startLadderZ));
			
			wallHeight = 5;
			wallDepth = 0.1f;
			float wallDepth2 = 0.05f;
			float wallSpace = 2.5f, wallSpace2 = 4;
			
			mb.begin();
			float temp;
			for (int i = 0; (temp = -terrain.getWidth() * 3 + i * wallSpace) <= terrain.getWidth() * 3; i++) {
				Node wall = mb.node();
				wall.translation.set(temp, wallHeight / 2, terrain.getDepth() * 1.5f);
				BoxShapeBuilder.build(mb.part(wall.id, GL20.GL_TRIANGLES, VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal, material), wallDepth, wallHeight, wallDepth);
			}
			
			for (int i = 1; (temp = wallHeight - i * wallSpace2) >= 0; i++) {
				Node wall = mb.node();
				wall.translation.set(0, temp, terrain.getDepth() * 1.5f);
				BoxShapeBuilder.build(mb.part(wall.id, GL20.GL_TRIANGLES, VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal, material), terrain.getWidth() * 6, wallDepth2, wallDepth2);
			}
			
			Node wallUpper = mb.node();
			wallUpper.translation.set(0, wallHeight, terrain.getDepth() * 1.5f);
			BoxShapeBuilder.build(mb.part(wallUpper.id, GL20.GL_TRIANGLES, VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal, material), terrain.getWidth() * 6, wallDepth, wallDepth);
			
			
			for (int i = 0; (temp = -terrain.getDepth() * 1.5f + i * wallSpace) <= terrain.getDepth() * 1.5f; i++) {
				Node wall = mb.node();
				wall.translation.set(terrain.getWidth() * 3, wallHeight / 2, temp);
				BoxShapeBuilder.build(mb.part(wall.id, GL20.GL_TRIANGLES, VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal, material), wallDepth, wallHeight, wallDepth);
			}
			
			for (int i = 1; (temp = wallHeight - i * wallSpace2) >= 0; i++) {
				Node wall = mb.node();
				wall.translation.set(terrain.getWidth() * 3, temp, 0);
				BoxShapeBuilder.build(mb.part(wall.id, GL20.GL_TRIANGLES, VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal, material), wallDepth2, wallDepth2, terrain.getDepth() * 3);
			}
			
			Node wallUpper2 = mb.node();
			wallUpper2.translation.set(terrain.getWidth() * 3, wallHeight, 0);
			BoxShapeBuilder.build(mb.part(wallUpper2.id, GL20.GL_TRIANGLES, VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal, material), wallDepth, wallDepth, terrain.getDepth() * 3);
			
			
			for (int i = 0; (temp = -terrain.getWidth() * 3 + i * wallSpace) <= terrain.getWidth() * 3; i++) {
				Node wall = mb.node();
				wall.translation.set(temp, wallHeight / 2, -terrain.getDepth() * 1.5f);
				BoxShapeBuilder.build(mb.part(wall.id, GL20.GL_TRIANGLES, VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal, material), wallDepth, wallHeight, wallDepth);
			}
			
			for (int i = 1; (temp = wallHeight - i * wallSpace2) >= 0; i++) {
				Node wall = mb.node();
				wall.translation.set(0, temp, -terrain.getDepth() * 1.5f);
				BoxShapeBuilder.build(mb.part(wall.id, GL20.GL_TRIANGLES, VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal, material), terrain.getWidth() * 6, wallDepth2, wallDepth2);
			}
			
			Node wallUpper3 = mb.node();
			wallUpper3.translation.set(0, wallHeight, -terrain.getDepth() * 1.5f);
			BoxShapeBuilder.build(mb.part(wallUpper3.id, GL20.GL_TRIANGLES, VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal, material), terrain.getWidth() * 6, wallDepth, wallDepth);
			
			modelInstances.add(new ModelInstance(mb.end()));
			
			
			float grassPlateWidth = terrain.getWidth() * 2.17f, grassPlateHeight = 0.01f, grassPlateDepth = terrain.getDepth() * 3;
			float grassPlateDepth1 = terrain.getWidth() * 1.83f, grassPlateWidth1 = terrain.getDepth();
			
			Material grassMaterial = new Material(ColorAttribute.createDiffuse(Color.GREEN.cpy().sub(0, 0.6f, 0, 0)));
			
			mb.begin();
			
			Node grassPlate = mb.node();
			grassPlate.translation.set(grassPlateWidth / 2 + terrain.getWidth() / 2 + 10, 0.01f, 0);
			BoxShapeBuilder.build(mb.part(grassPlate.id, GL20.GL_TRIANGLES, VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal, grassMaterial), grassPlateWidth, grassPlateHeight, grassPlateDepth);
			
			Node grassPlate1 = mb.node();
			grassPlate1.translation.set(0, 0.01f, terrain.getDepth() / 2 + grassPlateWidth / 2);
			BoxShapeBuilder.build(mb.part(grassPlate1.id, GL20.GL_TRIANGLES, VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal, grassMaterial), grassPlateWidth1, grassPlateHeight, grassPlateDepth1);
			
			Node grassPlate2 = mb.node();
			grassPlate2.translation.set(0, 0.01f, -(terrain.getDepth() / 2 + grassPlateWidth / 2));
			BoxShapeBuilder.build(mb.part(grassPlate2.id, GL20.GL_TRIANGLES, VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal, grassMaterial), grassPlateWidth1, grassPlateHeight, grassPlateDepth1);
			
			modelInstances.add(new ModelInstance(mb.end()));
			
			
			float fDoorRadius = ladderRadius, fDoorWidth = 15;
			
			Material fDoorMaterial = new Material(ColorAttribute.createDiffuse(Color.GRAY));
			
			mb.begin();
			
			Node fDoor1 = mb.node();
			fDoor1.translation.set(fDoorWidth / 2, ladderHeight / 2 - 6, 0);
			fDoor1.rotation.setEulerAngles(0, -25, 0);
			CylinderShapeBuilder.build(mb.part(fDoor1.id, GL20.GL_TRIANGLES, VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal, fDoorMaterial), fDoorRadius, ladderHeight / 2, fDoorRadius, divisions);
			
			Node fDoor2 = mb.node();
			fDoor2.translation.set(-fDoorWidth / 2, ladderHeight / 2 - 6, 0);
			fDoor2.rotation.setEulerAngles(0, -25, 0);
			CylinderShapeBuilder.build(mb.part(fDoor2.id, GL20.GL_TRIANGLES, VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal, fDoorMaterial), fDoorRadius, ladderHeight / 2, fDoorRadius, divisions);
			
			Node fDoor3 = mb.node();
			fDoor3.translation.set(0, ladderHeight / 2 - 1.8f, -1.95f);
			fDoor3.rotation.setEulerAngles(0, 0, 90);
			CylinderShapeBuilder.build(mb.part(fDoor3.id, GL20.GL_TRIANGLES, VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal, fDoorMaterial), fDoorRadius, fDoorWidth, fDoorRadius, divisions);
			
			Node fDoor4 = mb.node();
			fDoor4.translation.set(fDoorWidth / 2, ladderHeight / 2 - 6, -1.95f);
			CylinderShapeBuilder.build(mb.part(fDoor4.id, GL20.GL_TRIANGLES, VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal, fDoorMaterial), fDoorRadius, ladderHeight / 2 - 1, fDoorRadius, divisions);
			
			Node fDoor5 = mb.node();
			fDoor5.translation.set(-fDoorWidth / 2, ladderHeight / 2 - 6, -1.95f);
			CylinderShapeBuilder.build(mb.part(fDoor5.id, GL20.GL_TRIANGLES, VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal, fDoorMaterial), fDoorRadius, ladderHeight / 2 - 1, fDoorRadius, divisions);
			
			Model fDoor = mb.end();
			
			modelInstances.add(new ModelInstance(fDoor, terrain.getWidth() * 1.7f, 0, terrain.getDepth() / 2));
			modelInstances.add(new ModelInstance(fDoor, terrain.getWidth() * 1.7f, 0, -terrain.getDepth() / 2));
			modelInstances.get(modelInstances.size() - 1).transform.rotate(0, 1, 0, 180);
		}
		
		private MeshPartBuilder part(MeshPartBuilder input, float width, float height) {
			input.setUVRange(0, 0, height, width);
			
			return input;
		}

		@Override
		public boolean hasOwnTerrain() {
			
			return false;
		}

		@Override
		public Color getThemeColor() {
			
			return null;
		}
		
	},
	
	VERYHARD{

		@Override
		public void createModels(Terrain terrain) {
			ModelBuilder mb = new ModelBuilder();
			
			Material material = new Material(ColorAttribute.createDiffuse(Color.GRAY));
			Material standBWMaterial = new Material(ColorAttribute.createDiffuse(Color.GRAY.cpy().add(0.25f, 0.25f, 0.25f, 0)));
			Material plateMaterial = new Material(ColorAttribute.createDiffuse(Color.LIGHT_GRAY));
			Material wallMaterial = new Material(ColorAttribute.createDiffuse(Color.DARK_GRAY.cpy().add(0.05f, 0.05f, 0, 0)));
			Material ceilingMaterial = new Material(ColorAttribute.createDiffuse(Color.BLACK));
			Material stickMaterial = new Material(ColorAttribute.createDiffuse(Color.BLUE.cpy().sub(0, 0, 0.5f, 0)));
			
			float space = 50;
			float worldPlateWidth = terrain.getWidth() + space / 2, worldPlateDepth = terrain.getDepth() + space, worldPlateHeight = 0.01f;
			
			mb.begin();
			Node worldPlate = mb.node();
			worldPlate.translation.set(0, -0.1f, 0);
			BoxShapeBuilder.build(mb.part(worldPlate.id, GL20.GL_TRIANGLES, VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal, plateMaterial), worldPlateWidth, worldPlateHeight, worldPlateDepth);
			
			
			float standLevels = 15;
			float standD = 3, standH = standD;
			
			for(int i = 0; i < standLevels; i++) {
				Vector3 posW = new Vector3(worldPlateWidth / 2 + standD / 2 + standD * i, standH / 2 + standD * i + worldPlate.translation.y, 0);
				Vector3 posD = new Vector3(0, standH / 2 + standD * i + worldPlate.translation.y, worldPlateDepth / 2 + standD / 2 + standD * i);
				
				Node stand1 = mb.node();
				stand1.translation.set(posD);
				BoxShapeBuilder.build(mb.part(stand1.id, GL20.GL_TRIANGLES, VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal, material), worldPlateWidth, standH, standD);
				
				Node stand2 = mb.node();
				stand2.translation.set(posW);
				BoxShapeBuilder.build(mb.part(stand2.id, GL20.GL_TRIANGLES, VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal, material), standD, standH, worldPlateDepth);
				
				Node stand3 = mb.node();
				stand3.translation.set(posD).scl(1, 1, -1);
				BoxShapeBuilder.build(mb.part(stand3.id, GL20.GL_TRIANGLES, VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal, material), worldPlateWidth, standH, standD);
				
				Node stand4 = mb.node();
				stand4.translation.set(posW).scl(-1, 1, 1);
				BoxShapeBuilder.build(mb.part(stand4.id, GL20.GL_TRIANGLES, VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal, material), standD, standH, worldPlateDepth);
			}
			
			float standBWSize = standD * standLevels, standBWHeight = standD * (standLevels + 10);
			Vector3 pos = new Vector3(worldPlateWidth / 2 + standBWSize / 2, standBWHeight / 2, worldPlateDepth / 2 + standBWSize / 2);
			
			Node standBW1 = mb.node(); //standBetween
			standBW1.translation.set(pos);
			BoxShapeBuilder.build(mb.part(standBW1.id, GL20.GL_TRIANGLES, VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal, standBWMaterial), standBWSize, standBWHeight, standBWSize);
			
			Node standBW2 = mb.node(); //standBetween
			standBW2.translation.set(pos).scl(-1, 1, 1);
			BoxShapeBuilder.build(mb.part(standBW2.id, GL20.GL_TRIANGLES, VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal, standBWMaterial), standBWSize, standBWHeight, standBWSize);
			
			Node standBW3 = mb.node(); //standBetween
			standBW3.translation.set(pos).scl(1, 1, -1);
			BoxShapeBuilder.build(mb.part(standBW3.id, GL20.GL_TRIANGLES, VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal, standBWMaterial), standBWSize, standBWHeight, standBWSize);
			
			Node standBW4 = mb.node(); //standBetween
			standBW4.translation.set(pos).scl(-1, 1, -1);
			BoxShapeBuilder.build(mb.part(standBW4.id, GL20.GL_TRIANGLES, VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal, standBWMaterial), standBWSize, standBWHeight, standBWSize);
			
			Node wall1 = mb.node();
			wall1.translation.set(0, standBWHeight / 2, worldPlateDepth / 2 + standBWSize / 2 + standD * standLevels / 2);
			BoxShapeBuilder.build(mb.part(wall1.id, GL20.GL_TRIANGLES, VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal, wallMaterial), worldPlateWidth, standBWHeight, worldPlateHeight);
			
			Node wall2 = mb.node();
			wall2.translation.set(worldPlateWidth / 2 + standBWSize / 2 + standD * standLevels / 2, standBWHeight / 2, 0);
			BoxShapeBuilder.build(mb.part(wall2.id, GL20.GL_TRIANGLES, VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal, wallMaterial), worldPlateHeight, standBWHeight, worldPlateDepth);
			
			Node wall3 = mb.node();
			wall3.translation.set(wall1.translation).scl(1, 1, -1);
			BoxShapeBuilder.build(mb.part(wall3.id, GL20.GL_TRIANGLES, VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal, wallMaterial), worldPlateWidth, standBWHeight, worldPlateHeight);
			
			Node wall4 = mb.node();
			wall4.translation.set(wall2.translation).scl(-1, 1, 1);
			BoxShapeBuilder.build(mb.part(wall4.id, GL20.GL_TRIANGLES, VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal, wallMaterial), worldPlateHeight, standBWHeight, worldPlateDepth);
			
			
			float ceilingWidth = (worldPlateWidth / 2 + standBWSize / 2 + standD * standLevels / 2) * 2 , ceilingDepth = (worldPlateDepth / 2 + standBWSize / 2 + standD * standLevels / 2) * 2;
			Node ceiling = mb.node();
			ceiling.translation.set(0, standBWHeight, 0);
			BoxShapeBuilder.build(mb.part(ceiling.id, GL20.GL_TRIANGLES, VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal, ceilingMaterial), ceilingWidth, worldPlateHeight, ceilingDepth);
			
			float stickD = 0.5f, stickSpace = 4;
			float stickH1 = standBWHeight - stickD / 2, stickH2 = standBWHeight - stickSpace;
			
			Node ceilingStick = mb.node();
			ceilingStick.translation.set(0, stickH1, 0);
			BoxShapeBuilder.build(mb.part(ceilingStick.id, GL20.GL_TRIANGLES, VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal, stickMaterial), stickD, stickD, ceilingDepth);
			
			Node ceilingStick1 = mb.node();
			ceilingStick1.translation.set(0, stickH2, 0);
			BoxShapeBuilder.build(mb.part(ceilingStick1.id, GL20.GL_TRIANGLES, VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal, stickMaterial), stickD, stickD, ceilingDepth);
			
			Node ceilingStick3 = mb.node();
			ceilingStick3.translation.set(0, stickH1, ceilingDepth / 4);
			BoxShapeBuilder.build(mb.part(ceilingStick3.id, GL20.GL_TRIANGLES, VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal, stickMaterial), ceilingWidth, stickD, stickD);
			
			Node ceilingStick4 = mb.node();
			ceilingStick4.translation.set(0, stickH2, ceilingDepth / 4);
			BoxShapeBuilder.build(mb.part(ceilingStick4.id, GL20.GL_TRIANGLES, VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal, stickMaterial), ceilingWidth, stickD, stickD);
			
			Node ceilingStick5 = mb.node();
			ceilingStick5.translation.set(0, stickH1, -ceilingDepth / 4);
			BoxShapeBuilder.build(mb.part(ceilingStick5.id, GL20.GL_TRIANGLES, VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal, stickMaterial), ceilingWidth, stickD, stickD);
			
			Node ceilingStick6 = mb.node();
			ceilingStick6.translation.set(0, stickH2, -ceilingDepth / 4);
			BoxShapeBuilder.build(mb.part(ceilingStick6.id, GL20.GL_TRIANGLES, VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal, stickMaterial), ceilingWidth, stickD, stickD);
			
			Node ceilingStick7 = mb.node();
			ceilingStick7.translation.set(0, stickH1, 0);
			BoxShapeBuilder.build(mb.part(ceilingStick7.id, GL20.GL_TRIANGLES, VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal, stickMaterial), ceilingWidth, stickD, stickD);
			
			Node ceilingStick8 = mb.node();
			ceilingStick8.translation.set(0, stickH2, 0);
			BoxShapeBuilder.build(mb.part(ceilingStick8.id, GL20.GL_TRIANGLES, VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal, stickMaterial), ceilingWidth, stickD, stickD);
			
			modelInstances.add(new ModelInstance(mb.end()));
			
			
			float chairSize = standD - 1, chairSize2 = 0.1f, chairSpace = 4.5f;
			
			Material chairMaterial = new Material(ColorAttribute.createDiffuse(Color.WHITE));
			
			mb.begin();
			Node chairSeat = mb.node();
			chairSeat.translation.set(0, 0, chairSize / 2 + chairSize2 / 2);
			BoxShapeBuilder.build(mb.part(chairSeat.id, GL20.GL_TRIANGLES, VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal, chairMaterial), chairSize, chairSize2, chairSize);
			
			Node chairBack = mb.node();
			chairBack.rotation.setEulerAngles(0, 90, 0);
			chairBack.translation.set(0, chairSize / 2, 0);
			BoxShapeBuilder.build(mb.part(chairBack.id, GL20.GL_TRIANGLES, VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal, chairMaterial), chairSize, chairSize2, chairSize);
			
			Model chairM = mb.end(), teamM = EntityType.TEAMMATE.createEntityModel(), oppM = EntityType.OPPONENT.createEntityModel();//We're creating brand new player models as we have to make them sit (change their nodes transforms)
			
			
			float elbowDegrees = -83, shoulderDg = 15;
			
			teamM.getNode("hipL").rotation.setEulerAngles(0, -90, 0);
			teamM.getNode("hipR").rotation.setEulerAngles(0, -90, 0);
			
			teamM.getNode("shoulderL").rotation.setEulerAngles(-shoulderDg, 0, 0);
			teamM.getNode("shoulderR").rotation.setEulerAngles(shoulderDg, 0, 0);
			
			teamM.getNode("elbowL").rotation.setEulerAngles(0, elbowDegrees, 0);
			teamM.getNode("elbowR").rotation.setEulerAngles(0, elbowDegrees, 0);
			
			teamM.getNode("kneeL").rotation.setEulerAngles(0, 90, 0);
			teamM.getNode("kneeR").rotation.setEulerAngles(0, 90, 0);
			
			oppM.getNode("hipL").rotation.setEulerAngles(0, -90, 0);
			oppM.getNode("hipR").rotation.setEulerAngles(0, -90, 0);
			
			oppM.getNode("shoulderL").rotation.setEulerAngles(-shoulderDg, 0, 0);
			oppM.getNode("shoulderR").rotation.setEulerAngles(shoulderDg, 0, 0);
			
			oppM.getNode("elbowL").rotation.setEulerAngles(0, elbowDegrees, 0);
			oppM.getNode("elbowR").rotation.setEulerAngles(0, elbowDegrees, 0);
			
			oppM.getNode("kneeL").rotation.setEulerAngles(0, 90, 0);
			oppM.getNode("kneeR").rotation.setEulerAngles(0, 90, 0);
			
			
			float temp, chairYOffset = chairSize / 4 - chairSize2 / 2, chairZOffset = chairSize / 4;
			for (int i = 0; i < standLevels; i++) {
				for (int j = 0; (temp = -worldPlateWidth / 2 + chairSize / 2 + chairSpace * j) < worldPlateWidth / 2 - chairSize / 2; j++) {
					ModelInstance tempChair = new ModelInstance(chairM, temp, standH / 2 + standD * i + worldPlate.translation.y + chairSize - chairYOffset, worldPlateDepth / 2 + standD / 2 + standD * i + chairZOffset);
					tempChair.transform.rotate(0, 1, 0, 180);
					modelInstances.add(tempChair);
					
					Vector3 tempVecOff = new Vector3(0, 0, -1.5f);
					createPlayerInstance(teamM, oppM, tempChair, tempVecOff);
					
					ModelInstance tempChair2 = new ModelInstance(chairM, temp, standH / 2 + standD * i + worldPlate.translation.y + chairSize - chairYOffset, -(worldPlateDepth / 2 + standD / 2 + standD * i + chairZOffset));
					tempChair2.transform.rotate(0, 1, 0, 0);
					modelInstances.add(tempChair2);
					
					createPlayerInstance(teamM, oppM, tempChair2, tempVecOff.scl(1, 1, -1));
				}
				
				for (int j = 0; (temp = -worldPlateDepth / 2 + chairSize / 2 + chairSpace * j) < worldPlateDepth / 2 - chairSize / 2; j++) {
					ModelInstance tempChair = new ModelInstance(chairM, worldPlateWidth / 2 + standD / 2 + standD * i + chairZOffset, standH / 2 + standD * i + worldPlate.translation.y + chairSize - chairYOffset, temp);
					tempChair.transform.rotate(0, 1, 0, -90);
					modelInstances.add(tempChair);
					
					Vector3 tempVecOff = new Vector3(-1.5f, 0, 0);
					createPlayerInstance(teamM, oppM, tempChair, tempVecOff);
					
					ModelInstance tempChair2 = new ModelInstance(chairM, -(worldPlateWidth / 2 + standD / 2 + standD * i + chairZOffset), standH / 2 + standD * i + worldPlate.translation.y + chairSize - chairYOffset, temp);
					tempChair2.transform.rotate(0, 1, 0, 90);
					modelInstances.add(tempChair2);
					
					createPlayerInstance(teamM, oppM, tempChair2, tempVecOff.scl(-1, 1, 1));
				}
			}
			
		}
		
		private void createPlayerInstance(Model teamM, Model oppM, ModelInstance tempChair, Vector3 offset) {
			float playerYOffset = Player.scale3;
			
			if (MathUtils.random(1, 10) > 2) {
				Model tempPlayerM;
				ModelInstance tempPlayer;
				if (MathUtils.randomBoolean())
					tempPlayerM = teamM;
				else
					tempPlayerM = oppM;

				tempPlayer = new ModelInstance(tempPlayerM, tempChair.transform.cpy().trn(0, playerYOffset, 0).trn(offset));
				inGameModelInstances.add(tempPlayer);
			}
		}

		@Override
		public boolean hasOwnTerrain() {
			
			return false;
		}

		@Override
		public Color getThemeColor() {
			
			return null;
		}
		
	},
	
	CHALLENGE{

		@Override
		public void createModels(Terrain terrain) {
			
			
		}

		@Override
		public boolean hasOwnTerrain() {
			
			return false;
		}

		@Override
		public Color getThemeColor() {
			
			return null;
		}
		
	},
	
	MINIGAME{

		@Override
		public void createModels(Terrain terrain) {
			
			
		}

		@Override
		public boolean hasOwnTerrain() {
			
			return false;
		}

		@Override
		public Color getThemeColor() {
			
			return null;
		}
		
	};
	
	private static Model customTerrainModel;
	public static final ArrayList<ModelInstance> modelInstances = new ArrayList<ModelInstance>();
	public static final ArrayList<ModelInstance> inGameModelInstances = new ArrayList<ModelInstance>();
	
	
	public abstract void createModels(Terrain terrain);
	
	public abstract Color getThemeColor();
	
	public abstract boolean hasOwnTerrain();
	
	public void dispose() {
		if(customTerrainModel != null) {
			customTerrainModel.dispose();
			customTerrainModel = null;
		}
		
		while(modelInstances.size() > 0) {
			int index = modelInstances.size() - 1;
			
			Model temp = modelInstances.get(index).model;
			
			try {
				temp.dispose();
			}catch (Exception e) {
				System.out.print(temp);
				System.out.println(" already disposed!");
			}
			modelInstances.remove(index);
		}
		
		while(inGameModelInstances.size() > 0) {
			int index = inGameModelInstances.size() - 1;
			
			Model temp = inGameModelInstances.get(index).model;
			
			try {
				temp.dispose();
			}catch (Exception e) {
				System.out.print(temp);
				System.out.println(" already disposed!");
			}
			inGameModelInstances.remove(index);
		}
	}
	
	public ModelInstance getCustomTerrainModelInstance() {
		if(hasOwnTerrain())
			return new ModelInstance(customTerrainModel, 0, 0, 0);
		
		return null;
	}
	
}
