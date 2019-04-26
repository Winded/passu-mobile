import Vue from "nativescript-vue";

import { PasswordDatabase } from 'passu-lib';

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
