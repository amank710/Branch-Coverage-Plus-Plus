import {createSlice} from "@reduxjs/toolkit";

const initialState = {
    testFile: "",
};

const testFileSlice = createSlice({
    name: "testFile",
    initialState,
    reducers: {
        setTestFile(state, action) {
            state["testFile"] = action.payload.testFile;
        }
    }
});

export const {setTestFile} = testFileSlice.actions;

export default testFileSlice.reducer;