if (WEBGL.isWebGLAvailable() === false) {
    document.body.appendChild(WEBGL.getWebGLErrorMessage());
}

var container, stats;
var camera, scene, renderer, light;
var controls, water, sky, sphere;
init();
animate();

function init() {
    renderer = new THREE.WebGLRenderer();
    renderer.setPixelRatio(window.devicePixelRatio);
    renderer.setSize(window.innerWidth, window.innerHeight);
    document.body.appendChild(renderer.domElement);
    //
    scene = new THREE.Scene();
    //
    camera = new THREE.PerspectiveCamera(
        55,
        window.innerWidth / window.innerHeight,
        1,
        20000
    );
    camera.position.set(30, 30, 100);
    //
    light = new THREE.DirectionalLight(0xffffff, 0.8);
    scene.add(light);
    // Water
    var waterGeometry = new THREE.PlaneBufferGeometry(10000, 10000);
    water = new THREE.Water(waterGeometry, {
        textureWidth: 512,
        textureHeight: 512,
        waterNormals: new THREE.TextureLoader().load(
            "/js/waternormals.jpg",
            function (texture) {
                texture.wrapS = texture.wrapT = THREE.RepeatWrapping;
            }
        ),
        alpha: 1.0,
        sunDirection: light.position.clone().normalize(),
        sunColor: 0xffffff,
        waterColor: 0x001e0f,
        distortionScale: 3.7,
        fog: scene.fog !== undefined
    });
    water.rotation.x = -Math.PI / 2;
    scene.add(water);
    // Skybox
    sky = new THREE.Sky();
    sky.scale.setScalar(10000);
    scene.add(sky);
    var uniforms = sky.material.uniforms;
    uniforms["turbidity"].value = 10;
    uniforms["rayleigh"].value = 2;
    uniforms["luminance"].value = 1;
    uniforms["mieCoefficient"].value = 0.005;
    uniforms["mieDirectionalG"].value = 0.8;
    var parameters = {
        distance: 400,
        inclination: 0.49,
        azimuth: 0.205
    };
    var cubeCamera = new THREE.CubeCamera(1, 20000, 256);
    cubeCamera.renderTarget.texture.generateMipmaps = true;
    cubeCamera.renderTarget.texture.minFilter = THREE.LinearMipMapLinearFilter;
    function updateSun() {
        var theta = Math.PI * (parameters.inclination - 0.5);
        var phi = 2 * Math.PI * (parameters.azimuth - 0.5);
        light.position.x = parameters.distance * Math.cos(phi);
        light.position.y =
            parameters.distance * Math.sin(phi) * Math.sin(theta);
        light.position.z =
            parameters.distance * Math.sin(phi) * Math.cos(theta);
        sky.material.uniforms["sunPosition"].value = light.position.copy(
            light.position
        );
        water.material.uniforms["sunDirection"].value
            .copy(light.position)
            .normalize();
        cubeCamera.update(renderer, scene);
    }
    updateSun();
    //
    var geometry = new THREE.IcosahedronBufferGeometry(20, 1);
    var count = geometry.attributes.position.count;
    var colors = [];
    var color = new THREE.Color();
    for (var i = 0; i < count; i += 3) {
        color.setHex(Math.random() * 0xffffff);
        colors.push(color.r, color.g, color.b);
        colors.push(color.r, color.g, color.b);
        colors.push(color.r, color.g, color.b);
    }
    geometry.addAttribute("color", new THREE.Float32BufferAttribute(colors, 3));

    var material = new THREE.MeshStandardMaterial({
        vertexColors: THREE.VertexColors,
        roughness: 0.0,
        flatShading: true,
        envMap: cubeCamera.renderTarget.texture,
        side: THREE.DoubleSide
    });
    sphere = new THREE.Mesh(geometry, material);
    //scene.add(sphere);

    //
    // controls = new THREE.OrbitControls(camera, renderer.domElement);
    // controls.maxPolarAngle = Math.PI * 0.495;
    // controls.target.set(0, 10, 0);
    // controls.minDistance = 40.0;
    // controls.maxDistance = 200.0;
    //camera.lookAt(controls.target);
    //
    //stats = new Stats();
    //container.appendChild(stats.dom);
    // GUI
    // var gui = new dat.GUI();
    // var folder = gui.addFolder('Sky');
    // folder.add(parameters, 'inclination', 0, 0.5, 0.0001).onChange(updateSun);
    // folder.add(parameters, 'azimuth', 0, 1, 0.0001).onChange(updateSun);
    // folder.open();
    // var uniforms = water.material.uniforms;
    // var folder = gui.addFolder('Water');
    // folder.add(uniforms.distortionScale, 'value', 0, 8, 0.1).name('distortionScale');
    // folder.add(uniforms.size, 'value', 0.1, 10, 0.1).name('size');
    // folder.add(uniforms.alpha, 'value', 0.9, 1, .001).name('alpha');
    // folder.open();
    // //
    // window.addEventListener('resize', onWindowResize, false);
}
function onWindowResize() {
    camera.aspect = window.innerWidth / window.innerHeight;
    camera.updateProjectionMatrix();
    renderer.setSize(window.innerWidth, window.innerHeight);
}
function animate() {
    requestAnimationFrame(animate);
    render();
    //stats.update();
}
function render() {
    var p = performance.now();
    var time = p * 0.001;
    var sun = p * 0.0001;
    // sphere.position.y = Math.sin(time) * 20 + 5;
    // sphere.rotation.x = time * 0.5;
    // sphere.rotation.z = time * 0.51;
    water.material.uniforms["time"].value += 1.0 / 60.0;
    sky.material.uniforms["sunPosition"].value.y = Math.sin(sun) * 10 + 5;
    renderer.render(scene, camera);
}