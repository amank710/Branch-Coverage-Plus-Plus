import {createSlice} from "@reduxjs/toolkit";

const initialState = {
    pathCoverage: {
        pathCoverageMetadata: "",
        lineHits: {},
        uncoveredPaths: ""
    },
    pathState: false
};

const pathCoverageSlice = createSlice({
    name: "pathCoverage",
    initialState,
    reducers: {
        setPathCoverage(state, action) {
            state["pathCoverage"] = action.payload.pathCoverage;
        },
        setPathState(state, action) {
            state["pathState"] = action.payload.pathState;
        }
    }
});

export const {setPathCoverage, setPathState} = pathCoverageSlice.actions;
export default pathCoverageSlice.reducer;