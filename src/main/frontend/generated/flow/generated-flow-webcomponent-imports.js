import { injectGlobalWebcomponentCss } from 'Frontend/generated/jar-resources/theme-util.js';

import 'Frontend/generated/jar-resources/flow-component-renderer.js';
import '@vaadin/polymer-legacy-adapter/style-modules.js';
import '@vaadin/combo-box/src/vaadin-combo-box.js';
import 'Frontend/generated/jar-resources/comboBoxConnector.js';
import 'Frontend/generated/jar-resources/vaadin-grid-flow-selection-column.js';
import '@vaadin/radio-group/src/vaadin-radio-group.js';
import '@vaadin/grid/src/vaadin-grid-tree-toggle.js';
import '@vaadin/radio-group/src/vaadin-radio-button.js';
import '@vaadin/tooltip/src/vaadin-tooltip.js';
import '@vaadin/context-menu/src/vaadin-context-menu.js';
import 'Frontend/generated/jar-resources/contextMenuConnector.js';
import 'Frontend/generated/jar-resources/contextMenuTargetConnector.js';
import '@vaadin/multi-select-combo-box/src/vaadin-multi-select-combo-box.js';
import '@vaadin/grid/src/vaadin-grid.js';
import '@vaadin/grid/src/vaadin-grid-column.js';
import '@vaadin/grid/src/vaadin-grid-sorter.js';
import '@vaadin/checkbox/src/vaadin-checkbox.js';
import 'Frontend/generated/jar-resources/gridConnector.ts';
import '@vaadin/button/src/vaadin-button.js';
import '@vaadin/text-field/src/vaadin-text-field.js';
import '@vaadin/dialog/src/vaadin-dialog.js';
import '@vaadin/vertical-layout/src/vaadin-vertical-layout.js';
import '@vaadin/horizontal-layout/src/vaadin-horizontal-layout.js';
import 'Frontend/generated/jar-resources/disableOnClickFunctions.js';
import '@vaadin/grid/src/vaadin-grid-column-group.js';
import 'Frontend/generated/jar-resources/lit-renderer.ts';
import '@vaadin/notification/src/vaadin-notification.js';
import '@vaadin/common-frontend/ConnectionIndicator.js';
import '@vaadin/vaadin-lumo-styles/sizing.js';
import '@vaadin/vaadin-lumo-styles/spacing.js';
import '@vaadin/vaadin-lumo-styles/style.js';
import '@vaadin/vaadin-lumo-styles/vaadin-iconset.js';
import 'Frontend/generated/jar-resources/ReactRouterOutletElement.tsx';

const loadOnDemand = (key) => {
  const pending = [];
  if (key === 'b8053c742becfd26ee86e87c097d83477473ceab640cb4740ed62326426e87e6') {
    pending.push(import('./chunks/chunk-5b4dff514d45d28288c18887591127f219e56a67da1d084484c2ad53ac0e87be.js'));
  }
  if (key === '44b37cdce44170fc18d3b32a1db76bc2f835725065575304737c9b8d6c41f0c1') {
    pending.push(import('./chunks/chunk-1561187b3f2fd14198bda847bb4dcde821b99e348ca524582600b7b89cb7b2f8.js'));
  }
  if (key === '0933643123e8df1113e4cf1b401556194fa73a7e9c2deb4bd98cd63e2254629b') {
    pending.push(import('./chunks/chunk-ea52b5077d661d96460e5c72d81d81cfebddd58008ec657a29e981da471e29f4.js'));
  }
  if (key === '9e58618c795eee0f0edc5cd221bfe682e9d3085968f5e6245664312d2a3841cb') {
    pending.push(import('./chunks/chunk-9de52295b9873b58dd2593d428352eb2a78dbe9e7e5bfa7fa43fa1e2997810a1.js'));
  }
  return Promise.all(pending);
}

window.Vaadin = window.Vaadin || {};
window.Vaadin.Flow = window.Vaadin.Flow || {};
window.Vaadin.Flow.loadOnDemand = loadOnDemand;
window.Vaadin.Flow.resetFocus = () => {
 let ae=document.activeElement;
 while(ae&&ae.shadowRoot) ae = ae.shadowRoot.activeElement;
 return !ae || ae.blur() || ae.focus() || true;
}