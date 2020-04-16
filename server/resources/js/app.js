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
import Notifications from "vue-notification";
import VueChatScroll from "vue-chat-scroll";
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
Vue.use(Notifications);
Vue.use(VueChatScroll);

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
