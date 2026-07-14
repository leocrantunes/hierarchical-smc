<template>
  <div class="wrapper pl-2 pt-2">
    <div class="wrapper">
      <select
        class="gen-option block w-full px-4 py-3 text-base text-gray-900 border border-gray-300 rounded-lg bg-gray-50 focus:ring-blue-500 focus:border-blue-500 dark:bg-gray-700 dark:border-gray-600 dark:placeholder-gray-400 dark:text-white dark:focus:ring-blue-500 dark:focus:border-blue-500"
        v-model="selectedGenOption" @change="update">
        <option disabled value="">Please select one</option>
        <option v-for="opt in options" :value="opt" :key="opt.id">{{ opt.name }}({{ opt.type }}) - P:{{ opt.packageOnly }}
        </option>
      </select>
      <button type="button" data-modal-target="small-modal"
        class="gen-option text-white bg-blue-700 hover:bg-blue-800 focus:ring-4 focus:ring-blue-300 font-medium rounded-lg text-base px-5 py-3 ml-2 dark:bg-blue-600 dark:hover:bg-blue-700 focus:outline-none dark:focus:ring-blue-800"
        @click="toggleModal" :disabled="selectedGenOption === ''" >Details</button>
      <button type="button"
        class="gen-option text-white bg-blue-700 hover:bg-blue-800 focus:ring-4 focus:ring-blue-300 font-medium rounded-lg text-base px-5 py-3 ml-2 dark:bg-blue-600 dark:hover:bg-blue-700 focus:outline-none dark:focus:ring-blue-800"
        @click="downloadCSVData">.CSV</button>
      <div v-show="loading" role="status" class="ml-2">
        <svg aria-hidden="true" class="inline w-10 h-10 mr-2 text-gray-200 animate-spin dark:text-gray-600 fill-blue-600"
          viewBox="0 0 100 101" fill="none" xmlns="http://www.w3.org/2000/svg">
          <path
            d="M100 50.5908C100 78.2051 77.6142 100.591 50 100.591C22.3858 100.591 0 78.2051 0 50.5908C0 22.9766 22.3858 0.59082 50 0.59082C77.6142 0.59082 100 22.9766 100 50.5908ZM9.08144 50.5908C9.08144 73.1895 27.4013 91.5094 50 91.5094C72.5987 91.5094 90.9186 73.1895 90.9186 50.5908C90.9186 27.9921 72.5987 9.67226 50 9.67226C27.4013 9.67226 9.08144 27.9921 9.08144 50.5908Z"
            fill="currentColor" />
          <path
            d="M93.9676 39.0409C96.393 38.4038 97.8624 35.9116 97.0079 33.5539C95.2932 28.8227 92.871 24.3692 89.8167 20.348C85.8452 15.1192 80.8826 10.7238 75.2124 7.41289C69.5422 4.10194 63.2754 1.94025 56.7698 1.05124C51.7666 0.367541 46.6976 0.446843 41.7345 1.27873C39.2613 1.69328 37.813 4.19778 38.4501 6.62326C39.0873 9.04874 41.5694 10.4717 44.0505 10.1071C47.8511 9.54855 51.7191 9.52689 55.5402 10.0491C60.8642 10.7766 65.9928 12.5457 70.6331 15.2552C75.2735 17.9648 79.3347 21.5619 82.5849 25.841C84.9175 28.9121 86.7997 32.2913 88.1811 35.8758C89.083 38.2158 91.5421 39.6781 93.9676 39.0409Z"
            fill="currentFill" />
        </svg>
        <span class="sr-only">Loading...</span>
      </div>
    </div>
    <div id="cy"></div>
  </div>
  <!-- Small Modal -->
  <div id="small-modal" tabindex="-1"
    class="gen-option fixed top-0 left-0 right-0 hidden w-full p-4 overflow-x-hidden overflow-y-auto md:inset-0 h-[calc(100%-1rem)] max-h-full">
    <div class="relative w-full max-w-md max-h-full">
      <!-- Modal content -->
      <div class="relative bg-white rounded-lg shadow dark:bg-gray-700">
        <!-- Modal header -->
        <div class="flex items-center justify-between p-5 border-b rounded-t dark:border-gray-600">
          <h3 class="text-xl font-medium text-gray-900 dark:text-white">
            Graph details
          </h3>
          <button type="button" @click="toggleModal"
            class="text-gray-400 bg-transparent hover:bg-gray-200 hover:text-gray-900 rounded-lg text-sm w-8 h-8 ml-auto inline-flex justify-center items-center dark:hover:bg-gray-600 dark:hover:text-white"
            data-modal-hide="small-modal">
            <svg class="w-3 h-3" aria-hidden="true" xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 14 14">
              <path stroke="currentColor" stroke-linecap="round" stroke-linejoin="round" stroke-width="2"
                d="m1 1 6 6m0 0 6 6M7 7l6-6M7 7l-6 6" />
            </svg>
            <span class="sr-only">Close modal</span>
          </button>
        </div>
        <!-- Modal body -->
        <div class="p-6 space-y-6">
          <p class="text-base leading-relaxed text-gray-500 dark:text-gray-400">
            <b>ID:</b> {{ selectedGenOption.id }}
          </p>
          <p class="text-base leading-relaxed text-gray-500 dark:text-gray-400">
            <b>Name:</b> {{ selectedGenOption.name }}
          </p>
          <p class="text-base leading-relaxed text-gray-500 dark:text-gray-400">
            <b>Type:</b> {{ selectedGenOption.type }}
          </p>
          <p class="text-base leading-relaxed text-gray-500 dark:text-gray-400">
            <b>Number of Classes:</b> {{ selectedGenOption.numberOfClasses }}
          </p>
          <p class="text-base leading-relaxed text-gray-500 dark:text-gray-400">
            <b>Package-only:</b> {{ selectedGenOption.packageOnly }}
          </p>
          <p class="text-base leading-relaxed text-gray-500 dark:text-gray-400">
            <b>Timestamp:</b> {{ selectedGenOption.timestamp }}
          </p>
          <p class="text-base leading-relaxed text-gray-500 dark:text-gray-400">
            <b>Number of packages:</b> {{ selectedGenOption.metrics?.numberOfPackages }}
          </p>
          <p class="text-base leading-relaxed text-gray-500 dark:text-gray-400">
            <b>Average of classes per package:</b> {{ selectedGenOption.metrics?.averageOfClassesPerPackage }}
          </p>
          <p class="text-base leading-relaxed text-gray-500 dark:text-gray-400">
            <b>Max depth:</b> {{ selectedGenOption.metrics?.maxDepth }}
          </p>
          <p class="text-base leading-relaxed text-gray-500 dark:text-gray-400">
            <b>Execution time (ms):</b> {{ selectedGenOption.metrics?.executionTimeMs }}
          </p>
          <p class="text-base leading-relaxed text-gray-500 dark:text-gray-400">
            <b>Fitness value:</b> {{ selectedGenOption.metrics?.fitness }}
          </p>
          <p class="text-base leading-relaxed text-gray-500 dark:text-gray-400">
            <b>MoJo.FM:</b> {{ selectedGenOption.metrics?.mojofm }}
          </p>
          <p class="text-base leading-relaxed text-gray-500 dark:text-gray-400">
            <b>Percentual commits:</b> {{ selectedGenOption.metrics?.percentualCommits }}
          </p>
        </div>
        <!-- Modal footer -->
        <div class="flex items-center p-6 space-x-2 border-t border-gray-200 rounded-b dark:border-gray-600">
          <button data-modal-hide="small-modal" type="button"
            class="text-white bg-blue-700 hover:bg-blue-800 focus:ring-4 focus:outline-none focus:ring-blue-300 font-medium rounded-lg text-sm px-5 py-2.5 text-center dark:bg-blue-600 dark:hover:bg-blue-700 dark:focus:ring-blue-800"
            @click="toggleModal">Close</button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import axios from 'axios';
import { Modal, ModalOptions } from 'flowbite';

const selectedGenOption = ref('');
const loading = ref(false);
const options = ref([]);
const modal = ref();

onMounted(() => {
  loading.value = true;
  axios
    .get(`https://hmd-gen-api.rj.r.appspot.com/graphs`)
    .then(response => {
      options.value = response.data;
      loading.value = false;
    });

  // set the modal menu element
  const $targetEl = document.getElementById('small-modal');

  // options with default values
  const mOptions: ModalOptions = {
    placement: 'center',
    backdrop: 'dynamic',
    backdropClasses: 'bg-gray-900 bg-opacity-50 dark:bg-opacity-80 fixed inset-0 z-40',
    closable: true
  };

  modal.value = new Modal($targetEl, mOptions);
})

function toggleModal() {
  modal.value.isVisible() ? modal.value.hide() : modal.value.show();
}

function update(): any {
  loading.value = true;
  axios
    .get(`https://hmd-gen-api.rj.r.appspot.com/graphs/${selectedGenOption.value.id}/elements`)
    .then(response => {
      let cy = cytoscape({
        container: document.getElementById('cy'),

        layout: {
          name: 'cola'
        },

        style: [
          {
            selector: 'node',
            css: {
              //'label': 'data(id)',
              'background-color': '#0047AB'
            }
          },
          {
            selector: 'node:parent',
            css: {
              'background-opacity': 0.1
            }
          },
          {
            selector: 'edge',
            css: {
              //'line-color': '#f92411' 	#0047AB
              'line-color': '#0047AB',
              'target-arrow-color': '#0047AB',
              'target-arrow-shape': 'chevron',
              'curve-style': 'bezier'
            }
          }
        ],
        elements: response.data
      });

      function makePopper(ele: any) {
        let ref = ele.popperRef(); // used only for positioning

        ele.tippy = tippy(ref, {
          // tippy options:
          content: () => {
            let content = document.createElement("div");

            content.innerHTML = ele.id();

            return content;
          },
          trigger: "manual" // probably want manual mode
        });
      }

      cy.ready(function () {
        cy.elements().forEach(function (ele: any) {
          makePopper(ele);
        });
      });

      cy.elements().unbind("mouseover");
      cy.elements().bind("mouseover", (event: any) => event.target.tippy.show());

      cy.elements().unbind("mouseout");
      cy.elements().bind("mouseout", (event: any) => event.target.tippy.hide());

      loading.value = false;
    });
}

function downloadCSVData() : any {
    let csv = 'id,name,type,numberOfClasses,packageOnly,timestamp,numberOfPackages,averageOfClassesPerPackage,maxDepth,executionTimeMs,fitness,mojofm,percentualCommits\n';
    
    options.value.forEach((row : any) => {
      csv += getData(row);
      csv += "\n";
    });
 
    const anchor = document.createElement('a');
    anchor.href = 'data:text/csv;charset=utf-8,' + encodeURIComponent(csv);
    anchor.target = '_blank';
    anchor.download = 'graphs.csv';
    anchor.click();
}

function getData(row: any) : any {
  if (row == null) 
    return '';

  return Object.keys(row).map(function(k) { 
    return typeof row[k] === 'object' ? getData(row[k]) : row[k]
  }).join(",");
}

</script>

<style>
body {
  font-family: helvetica;
  font-size: 14px;
}

.wrapper {
  display: flex;
}

#cy {
  position: absolute;
  left: 0;
  top: 0;
  width: 100%;
  height: 100%;
  z-index: 998;
}

h1 {
  opacity: 0.5;
  font-size: 1em;
}

.gen-option {
  z-index: 999;
}</style>
