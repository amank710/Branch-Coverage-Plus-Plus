import {configureStore} from "@reduxjs/toolkit";
import codeFileReducer from "./Reducers/codeFileSlice";
import testFileReducer from "./Reducers/testFileSlice";
import pathCoverageReducer from "./Reducers/pathCoverageSlice";
import chartReducer from "./Reducers/chartSlice";

const store = configureStore({
    reducer: {
        codeFile: codeFileReducer,
        testFile: testFileReducer,
        pathCoverage: pathCoverageReducer,
        chart: chartReducer,
    }
});

export default store;