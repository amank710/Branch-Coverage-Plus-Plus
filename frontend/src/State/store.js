import {configureStore} from "@reduxjs/toolkit";
import codeFileReducer from "./Reducers/codeFileSlice";
import testFileReducer from "./Reducers/testFileSlice";

const store = configureStore({
    reducer: {
        codeFile: codeFileReducer,
        testFile: testFileReducer
    }
});

export default store;