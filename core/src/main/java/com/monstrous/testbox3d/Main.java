package com.monstrous.testbox3d;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.VertexAttributes;
import com.badlogic.gdx.graphics.g3d.*;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.environment.DirectionalLight;
import com.badlogic.gdx.graphics.g3d.utils.CameraInputController;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;
import com.badlogic.gdx.math.Quaternion;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ScreenUtils;
import com.github.xpenatan.box3d.*;
import com.github.xpenatan.box3d.gdx.GdxBox3DConverter;
import com.github.xpenatan.jParser.loader.JParserLibraryLoaderListener;


// The Box3D code follows example from https://github.com/erincatto/box3d/blob/main/docs/hello.md
// We show a single cube dropping on a ground plane


public class Main extends ApplicationAdapter {
    private Model modelGround, modelBox;
    private ModelBatch modelBatch;
    private Array<ModelInstance> instances;
    private ModelInstance cubeInstance;
    private PerspectiveCamera cam;
    private CameraInputController camController;
    private Environment environment;
    private final Color backgroundColor = new Color(0.15f, 0.15f, 0.2f, 1f);
    private B3World world;
    private B3Body cubeBody;
    private final Vector3 cubePos = new Vector3();
    private final Quaternion cubeQuat = new Quaternion();

    @Override
    public void create() {

        // load Box3d native library
        JBox3DLoader.init(new JParserLibraryLoaderListener() {
            @Override
            public void onLoad(boolean b, Throwable throwable) {
                System.out.println("Loaded Box3d native, result: "+b);
            }
        });

        modelBatch = new ModelBatch();

        cam = new PerspectiveCamera(67, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        cam.position.set(10f, 10f, 10f);
        cam.lookAt(0, 0, 0);
        cam.near = 0.1f;
        cam.far = 500f;
        cam.update();

        camController = new CameraInputController(cam);
        Gdx.input.setInputProcessor(camController);

        instances = new Array<>();

        ModelBuilder mb = new ModelBuilder();
        modelGround = mb.createBox(100, 20, 100,
            new Material(ColorAttribute.createDiffuse(Color.GREEN)),
            VertexAttributes.Usage.Position | VertexAttributes.Usage.ColorPacked | VertexAttributes.Usage.Normal);
        instances.add( new ModelInstance(modelGround, 0, -10f, 0) );

        modelBox = mb.createBox(1, 1, 1,
            new Material(ColorAttribute.createDiffuse(Color.BLUE)),
            VertexAttributes.Usage.Position | VertexAttributes.Usage.ColorPacked | VertexAttributes.Usage.Normal);
        cubeInstance = new ModelInstance(modelBox, 0, 4f, 0);
        instances.add( cubeInstance );


        // define some lighting
        environment = new Environment();
        DirectionalLight light = new DirectionalLight();
        light.setDirection(0.3f, -0.8f, -0.2f);
        light.setColor(Color.LIGHT_GRAY);

        environment.add(light);

        // create physics world
        B3WorldDef worldDef = new B3WorldDef();
        B3Vec3 gravity = new B3Vec3(0f, -10f, 0f);
        System.out.println("gravity y = "+gravity.GetY());
        worldDef.SetGravity(gravity);
        world = new B3World(worldDef);

        // Create Ground body
        B3BodyDef groundBodyDef = new B3BodyDef();
        B3Vec3 groundPos = new B3Vec3(0, -10f, 0);
        float gy = groundPos.GetY();
        System.out.println("ground y = "+gy);
        groundBodyDef.SetPosition(groundPos);

        B3Body groundBody = world.CreateBody(groundBodyDef);

        B3Hull groundBox = B3Hull.CreateBox(50f, 10f, 50f);
        B3ShapeDef groundShapeDef = new B3ShapeDef();
        groundBody.CreateHullShape(groundShapeDef, groundBox);

        // Create a dynamic body
        B3BodyDef cubeBodyDef = new B3BodyDef();
        cubeBodyDef.SetType(2); // 2 = dynamic body
        cubeBodyDef.SetPosition(new B3Vec3(0, 8f, 0));
        cubeBody = world.CreateBody(cubeBodyDef);

        B3Hull cubeBox = B3Hull.CreateCube(1f);
        B3ShapeDef cubeShapeDef = new B3ShapeDef();
        cubeShapeDef.SetDensity(0.1f);
        B3SurfaceMaterial material = new B3SurfaceMaterial();
        material.SetFriction(0.3f);
        material.SetRestitution(0.9f);
        cubeShapeDef.SetBaseMaterial(material);
        // Note: the following doesn't work:
        //        cubeShapeDef.GetBaseMaterial().SetFriction(0.1f);

        cubeBody.CreateHullShape(cubeShapeDef, cubeBox);
    }

    @Override
    public void render() {
        camController.update();

        float timeStep = 1f/60f;
        int subStepCount = 4;

        world.Step(timeStep, subStepCount);

        B3Vec3 position = cubeBody.GetPosition();
        B3Quat rotation = cubeBody.GetRotation();

        // convert Box3D values to LibGDX values
        GdxBox3DConverter.toGdx(position, cubePos);
        //System.out.println("Position: "+cubePos);

        GdxBox3DConverter.toGdx(rotation, cubeQuat);

        // set transform of model instance to match dynamic object
        cubeInstance.transform.set(cubePos, cubeQuat);

        ScreenUtils.clear(backgroundColor, true);
        modelBatch.begin(cam);
        modelBatch.render(instances, environment);
        modelBatch.end();
    }

    @Override
    public void dispose() {

        world.Destroy();

        modelBatch.dispose();
        modelGround.dispose();
        modelBox.dispose();
    }
}
