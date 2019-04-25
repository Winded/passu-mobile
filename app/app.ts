import Vue from "nativescript-vue";

require('nativescript-nodeify');
import { PasswordDatabase } from 'passu/src/passu';

import Home from "./components/Home";

const db = new PasswordDatabase('test');

new Vue({

    template: `
        <Frame>
            <Home />
        </Frame>`,

    components: {
        Home
    }
}).$start();
