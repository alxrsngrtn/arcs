/**
 * @license
 * Copyright 2019 Google LLC.
 * This code may only be used under the BSD style license found at
 * http://polymer.github.io/LICENSE.txt
 * Code distributed by Google as part of this project is also
 * subject to an additional IP rights grant found at
 * http://polymer.github.io/PATENTS.txt
 */
import './file-pane.js';
import './output-pane.js';
import {DevShellLoader} from './loader.js';

import {Runtime} from '../../build/runtime/runtime.js';
import {Arc} from '../../build/runtime/arc.js';
import {IdGenerator} from '../../build/runtime/id.js';
import {Modality} from '../../build/runtime/modality.js';
import {ModalityHandler} from '../../build/runtime/modality-handler.js';
import {PecIndustry} from '../../build/platform/pec-industry-web.js';
import {RecipeResolver} from '../../build/runtime/recipe/recipe-resolver.js';
import {SlotComposer} from '../../build/runtime/slot-composer.js';
import {SlotDomConsumer} from '../../build/runtime/slot-dom-consumer.js';
import {StorageProviderFactory} from '../../build/runtime/storage/storage-provider-factory.js';
import {devtoolsInspectorFactory} from '../../build/devtools-connector/devtools-inspector.js';

const files = document.getElementById('file-pane');
const output = document.getElementById('output-pane');
const toggleFiles = document.getElementById('toggle-files');

import '../../modalities/dom/components/elements/corellia-xen/cx-input.js';
import '../../modalities/dom/components/elements/corellia-xen/cx-tabs.js';
import '../../modalities/dom/components/elements/corellia-xen/cx-button.js';
import '../../modalities/dom/components/elements/video-controller.js';
import '../../modalities/dom/components/elements/mic-input.js';
import '../../modalities/dom/components/elements/good-map.js';
import '../../modalities/dom/components/elements/geo-location.js';
import '../../modalities/dom/components/elements/model-input.js';
import '../../modalities/dom/components/elements/model-img.js';
import '../../modalities/dom/components/elements/dom-repeater.js';

let hot = false;

let ws = new WebSocket('ws://localhost:10101');
ws.onopen = e => {
  hot = true;
  console.log(`Established connection to HotReloadServer.`);
  ws.onmessage = msg => {
    const filepath = msg.data;
    // console.log('Registered modification to: ', filepath);
    const arcs = [window.arc];
    for (const inner of window.arc.innerArcsByParticle.values()) {
      arcs.push(...inner);
    }
    for (const arc of arcs) {
      for (const particle of arc.pec.particles) {
        if (particle.spec.implFile === filepath) {
          arc.pec.reboot(particle);
        }
      }
    }
  }
};
ws.onerror = e => {
  console.log(`No WebSocket connection found.`);
};

async function wrappedExecute() {
  SlotDomConsumer.clearCache();  // prevent caching of template strings
  document.dispatchEvent(new Event('clear-arcs-explorer'));
  output.reset();

  const loader = new DevShellLoader(files.getFileMap());
  const pecFactory = PecIndustry(loader);

  let manifest;
  try {
    const options = {loader, fileName: './manifest', throwImportErrors: true};
    manifest = await Runtime.parseManifest(files.getManifest(), options);
  } catch (e) {
    output.showError('Error in Manifest.parse', e);
    return;
  }

  // Here we get all files.
  if (hot) {
    let depsFiles = new Set();
    manifest.collectDependencies(depsFiles);
    depsFiles.delete('./manifest');
    ws.send(JSON.stringify([...depsFiles]));  
  }

  let arcIndex = 1;
  for (const recipe of [manifest.allRecipes[0]]) {
    const id = IdGenerator.newSession().newArcId('arc' + arcIndex++);
    const arcPanel = output.addArcPanel(id);

    const errors = new Map();
    if (!recipe.normalize({errors})) {
      arcPanel.showError('Error in recipe.normalize', [...errors.values()].join('\n'));
      continue;
    }

    const slotComposer = new SlotComposer({
      modalityName: Modality.Name.Dom,
      modalityHandler: ModalityHandler.domHandler,
      rootContainer: arcPanel.arcRoot,
      containers: {
        modal: arcPanel.arcModal,
        root: arcPanel.arcRoot,
      }
    });
    const storage = new StorageProviderFactory(id);
    const arc = new Arc({
      id,
      context: manifest,
      pecFactory,
      slotComposer,
      loader,
      storageProviderFactory: storage,
      inspectorFactory: devtoolsInspectorFactory
    });
    arcPanel.attachArc(arc);

    recipe.normalize();

    let resolvedRecipe = null;
    if (recipe.isResolved()) {
      resolvedRecipe = recipe;
    } else {
      const resolver = new RecipeResolver(arc);
      resolvedRecipe = await resolver.resolve(recipe);
      if (!resolvedRecipe) {
        arcPanel.showError('Error in RecipeResolver');
        continue;
      }  
    }

    try {
      await arc.instantiate(resolvedRecipe);
    } catch (e) {
      arcPanel.showError('Error in arc.instantiate', e);
      continue;
    }
    arcPanel.display(await Runtime.getArcDescription(arc));

    window.arc = arc;
  }
}

function execute() {
  wrappedExecute().catch(e => output.showError('Unhandled exception', e.stack));
}

function init() {
  const params = new URLSearchParams(window.location.search);
  const manifestParam = params.get('m') || params.get('manifest');
  if (manifestParam) {
    toggleFiles.click();
    files.seedManifest(manifestParam.split(';').map(m => `import '${m}'`));
  } else {
    const exampleManifest = `\
import 'https://$particles/Restaurants/Restaurants.recipes'
`;

    const exampleParticle = `\
defineParticle(({DomParticle, html}) => {
  return class extends DomParticle {
    get template() {
      return html\`<span>{{num}}</span> : <span>{{str}}</span>\`;
    }
    render({data}) {
      return data ? {num: data.num, str: data.txt} : {};
    }
  };
});`;

    files.seedExample(exampleManifest, exampleParticle);
  }
}

document.getElementById('execute').addEventListener('click', execute);
document.getElementById('export').addEventListener('click', files.exportFiles.bind(files));
toggleFiles.addEventListener('click', files.toggleFiles.bind(files));
files.setExecuteCallback(execute);
init();
