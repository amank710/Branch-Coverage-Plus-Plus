import './App.css';
import {BrowserRouter, Link, Route, Routes} from "react-router-dom";
import PathCoverageAppShell from "./Components/PathCoverageAppShell";
import {MantineProvider} from "@mantine/core";
import HomePage from "./Pages/home";
import ChartPage from "./Pages/chart_page";
import HighlightPage from "./Pages/highlight_page";

function App() {
    return (
        <MantineProvider>
            <BrowserRouter forceRefresh={false}>
                <PathCoverageAppShell />
            </BrowserRouter>
        </MantineProvider>
    );
}

export default App;
