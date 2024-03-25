import './App.css';
import {BrowserRouter} from "react-router-dom";
import PathCoverageAppShell from "./Components/PathCoverageAppShell";
import {MantineProvider} from "@mantine/core";

function App() {
  return (
      <MantineProvider>
          <BrowserRouter>
              <PathCoverageAppShell />
          </BrowserRouter>
      </MantineProvider>
  );
}

export default App;
