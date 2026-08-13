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
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ScreenUtils;
import com.github.xpenatan.box3d.*;
import com.github.xpenatan.jParser.loader.JParserLibraryLoaderListener;


// The Box3D code follows example from https://github.com/erincatto/box3d/blob/main/docs/hello.md

public class Main extends ApplicationAdapter {
    private Model modelGround, modelBox;
    private ModelBatch modelBatch;
    private Array<ModelInstance> instances;
    private PerspectiveCamera cam;
    private CameraInputController camController;
    private Environment environment;
    private final Color backgroundColor = new Color(0.15f, 0.15f, 0.2f, 1f);
    private B3World world;

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
        instances.add( new ModelInstance(modelBox, 0, 4f, 0) );


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

//        B3BodyDef groundBodyDef = new B3BodyDef();
//        B3Vec3 groundPos = new B3Vec3(0, -10f, 0);
//        //groundPos.Set(0f, -10f, 0f);
//        float gy = groundPos.GetY();
//        System.out.println("ground y = "+gy);
//        groundBodyDef.SetPosition(groundPos);
//
//        B3Body groundBody = world.CreateBody(groundBodyDef);
//
//        B3Hull groundBox = B3Hull.CreateBox(50f, 10f, 50f);
//        B3ShapeDef groundShapeDef = new B3ShapeDef();
//        groundBody.CreateHullShape(groundShapeDef, groundBox);

    }

    @Override
    public void render() {
        camController.update();

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
