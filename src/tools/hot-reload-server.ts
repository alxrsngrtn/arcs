/**
 * @license
 * Copyright (c) 2019 Google Inc. All rights reserved.
 * This code may only be used under the BSD style license found at
 * http://polymer.github.io/LICENSE.txt
 * Code distributed by Google as part of this project is also
 * subject to an additional IP rights grant found at
 * http://polymer.github.io/PATENTS.txt
 */

import WebSocket from 'ws';
import chokidar from 'chokidar';

const server = new WebSocket.Server({port: 10101});

console.log('Starting HRS!');

let conn = 0;

server.on('connection', ws => {
  const lconn = conn++;
  console.log(`Opened ${lconn}!`);
  const watchers = [];
  let msgcnt = 0;
  ws.on('message', msg => {
    const lmsgcnt = msgcnt++;
    watchers.forEach(w => w.close());
    watchers.length = 0;

    let fileset = JSON.parse(msg);
    for (const file of fileset) {
      const local = file.replace(/^https:\/\/\$particles\//,'./particles/')
    
      console.log(`${lconn}:${lmsgcnt} Watching: ${local}`);
      watchers.push(chokidar.watch(local).on('change', path => {
        console.log(`${lconn}:${lmsgcnt} Detected change: ${path}`);
        ws.send(file);
      }));
    }
  });
  ws.on('close', () => {
    console.log('disconnected');
    watchers.forEach(w => w.close());
    watchers.length = 0;
  });
  ws.on('error', e => console.log(`error, disconnecting?`));
});
