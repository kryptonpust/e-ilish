/**
 * First we will load all of this project's JavaScript dependencies which
 * includes Vue and other libraries. It is a great starting point when
 * building robust, powerful web applications using Vue and Laravel.
 */

require("./bootstrap");
import VueRouter from "vue-router";
import router from "./routes";
import tinymce from "tinymce/tinymce";
import VueScheduler from "v-calendar-scheduler";
import "v-calendar-scheduler/lib/main.css";
import Popover from "vue-js-popover";
import VueTerm from "vue-term";
//import draggable from 'vuedraggable'

// A theme is also required
import "tinymce/themes/silver/theme";

// // Any plugins you want to use has to be imported
import "tinymce/plugins/paste";
import "tinymce/plugins/link";
import "tinymce/plugins/quickbars";
import "tinymce/plugins/autoresize";
import "tinymce/plugins/code";
import "tinymce/plugins/image";
import "tinymce/plugins/imagetools";

window.Vue = require("vue");
Vue.use(VueRouter);
Vue.use(VueScheduler);
Vue.use(Popover);
Vue.use(VueTerm);
/**
 * The following block of code may be used to automatically register your
 * Vue components. It will recursively scan this directory for the Vue
 * components and automatically register them with their "basename".
 *
 * Eg. ./components/ExampleComponent.vue -> <example-component></example-component>
 */

// const files = require.context("./", true, /\.vue$/i);
// files.keys().map(key =>
//     Vue.component(
//         key
//             .split("/")
//             .pop()
//             .split(".")[0],
//         files(key).default
//     )
// );

Vue.component(
    "root-component",
    require("./components/RootComponent.vue").default
);

/**
 * Next, we will create a fresh Vue application instance and attach it to
 * the page. Then, you may begin adding components to this application
 * or customize the JavaScript scaffolding to fit your unique needs.
 */

const app = new Vue({
    el: "#app",
    router
});
//TinyMce
// tinymce.init({
//     selector: "#tinymce",
//     skin: "oxide",
//     plugins: [
// "link",
// "paste",
// "quickbars",
// "autoresize",
// "code",
// "image",
// "imagetools"
//     ],
//     min_height: 500,
//     images_upload_handler: function(blobInfo, success, failure) {
//         //console.log(blobInfo);
//         var formData = new FormData();
//         // formData.append=('image',blobInfo);
//         formData.append("image", blobInfo.blob());
//         axios.post("/api/imageupload", formData, {
//             headers: { "Content-Type": "multipart/form-data" }
//         })
//             .then(data => {
//                 success(data.data.location);
//                 //console.log(data);
//             })
//             .catch(error => {
//                 failure(error);
//                 //console.log(error);
//             });
//     }
// });

// tinymce.activeEditor.uploadImages(function (success) {
//     axios.post('api/imageupload', tinymce.activeEditor.getContent())
//     .then(data => {
//         console.log("Uploaded images and posted content as an ajax request.");
//     });
// });

//End TinyMce
//var THREE = require('three');

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
