import {Route, Routes} from "react-router-dom";
import HomePage from "../Pages/home";
import {AppShell} from "@mantine/core";

const PathCoverageAppShell = () => {

    return (
       <AppShell
           padding={"md"}
       >
           <AppShell.Header>
               <HomePage></HomePage>
           </AppShell.Header>
            <Routes>
                <Route path={'/'} element={HomePage}/>
            </Routes>
        </AppShell>
    )
};

export default PathCoverageAppShell;