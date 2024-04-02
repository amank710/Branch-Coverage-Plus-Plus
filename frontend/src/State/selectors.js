const selectCodeFile = (state) => state.codeFile;
const selectTestFile = (state) => state.testFile;

export const selectors = {
    selectCodeFile,
    selectTestFile
};

export default selectors;