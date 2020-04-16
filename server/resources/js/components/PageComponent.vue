<template>
  <div class="container">
    <div class="box">
      <div class="card">
        <header class="card-header">
          <p class="card-header-title title" style="color:red">Add New Page</p>
        </header>
        <div class="card-content">
          <div class="field">
            <label class="label">Title</label>
            <div class="control parent">
              <input
                v-model="title"
                :style="{'color': titlecolor}"
                class="input"
                type="text"
                placeholder="Enter a Title"
              >
              <Swatches
                class="childcolor"
                show-fallback
                v-model="titlecolor"
                colors="material-light"
                popover-to="left"
              ></Swatches>
            </div>
          </div>
          <div class="field">
            <!-- <div class="textarea" id="tinymce"></div> -->
            <editor :init="tinyinit"></editor>
          </div>
          <div class="field">
            <div class="control">
              <button @click="submit" class="button is-link">Submit</button>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import Editor from "@tinymce/tinymce-vue";
import * as notihelper from "../NotifiactionHandler.js";
import Swatches from "vue-swatches";
import "vue-swatches/dist/vue-swatches.min.css";
export default {
  components: {
    editor: Editor,
    Swatches
  },
  data() {
    return {
      title: null,
      titlecolor: "#000000",
      tinyinit: {
        skin: "oxide",
        plugins: [
          "advlist anchor autolink autoresize  charmap code codesample directionality emoticons fullpage fullscreen hr image importcss insertdatetime legacyoutput link" +
            " lists nonbreaking noneditable pagebreak paste preview quickbars save searchreplace spellchecker" +
            " tabfocus table template textpattern" +
            " toc visualblocks visualchars wordcount"
        ],
        menubar: "edit insert view format table tools",
        toolbar:
          "insertfile undo redo | fontsizeselect | bold italic font | forecolor backcolor | alignleft aligncenter alignright alignjustify | bullist numlist | link image",
        toolbar_drawer: "floating",
        templates: [
          {
            title: "Basic Template",
            description: "Basic Template",
            content: "<h1>Hello from Template</h1>"
          }
        ],
        style_formats: [
          {
            title: "Image Left",
            selector: "img",
            styles: {
              float: "left",
              margin: "0 10px 0 10px"
            }
          },
          {
            title: "Image Right",
            selector: "img",
            styles: {
              float: "right",
              margin: "0 10px 0 10px"
            }
          }
        ],
        branding: false,
        min_height: 500,
        images_upload_handler: function(blobInfo, success, failure) {
          //console.log(blobInfo);
          var formData = new FormData();
          // formData.append=('image',blobInfo);
          formData.append("image", blobInfo.blob());
          axios
            .post("/api/imageupload", formData, {
              headers: { "Content-Type": "multipart/form-data" }
            })
            .then(data => {
              success(data.data.location);
              //console.log(data);
            })
            .catch(error => {
              failure(error);
              //console.log(error);
            });
        }
      }
    };
  },
  methods: {
    submit() {
      if (this.title != null && this.titlecolor.length >= 7) {
        //console.log(this.data);
        tinymce.activeEditor.uploadImages(
          function(success) {
            axios
              .post(
                "/api/dataupload",
                {
                  title: this.title,
                  titlecolor: this.titlecolor,
                  data: tinymce.activeEditor.getContent()
                },
                { headers: { "content-type": "application/json" } }
              )
              .then(data => {
                tinymce.activeEditor.setContent("");
                this.title = null;
                this.$root.$emit("update");
                notihelper.notification.call(this, data);
              })
              .catch(error => {
                console.log(error);
                notihelper.noti_error.call(this, error);
              });
          }.bind(this)
        );
      } else {
        if (this.titlecolor.length < 7) {
          notihelper.noti_warn.call(this, "Color must be 6 digit String");
        } else {
          notihelper.noti_warn.call(this, "Title must Required!!");
        }
      }
    }
  }
};
</script>

<style lang="scss" scoped>
.parent {
  display: flex;
  align-items: center;
  justify-content: space-around;
  .childcolor {
    margin: 2px;
    padding-top: 5px;
  }
}
</style>
