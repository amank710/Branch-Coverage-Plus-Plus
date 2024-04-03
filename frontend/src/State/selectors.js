const selectCodeFile = (state) => state.codeFile;
const selectTestFile = (state) => state.testFile;
const selectPathCoverage = (state) => state.pathCoverage;
const selectChart = (state) => state.chart;

export const selectors = {
    selectCodeFile,
    selectTestFile,
    selectPathCoverage,
    selectChart
};

export default selectors;