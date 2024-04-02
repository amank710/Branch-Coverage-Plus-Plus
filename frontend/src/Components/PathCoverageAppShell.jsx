import {Link, redirect, Route, Routes} from "react-router-dom";
import HomePage from "../Pages/home";
import {AppShell, Burger, Group, NavLink, Title} from "@mantine/core";
import {useDisclosure} from "@mantine/hooks";
import ChartPage from "../Pages/chart_page";
import HighlightPage from "../Pages/highlight_page";

const PathCoverageAppShell = () => {
    const [desktopOpened, { toggle: toggleDesktop }] = useDisclosure(true);

    return (
        <AppShell
            header={{ height: 60 }}
            navbar={{
                width: 300,
                breakpoint: 'sm',
                collapsed: { desktop: !desktopOpened },
            }}
            padding="md"
        >
            <AppShell.Header>
                <Group h="100%" px="lg">
                    <Burger opened={desktopOpened} onClick={toggleDesktop} visibleFrom="sm" size="sm" />
                    <Title order={2} padding={"md"}>Path Coverage Plus Plus</Title>
                </Group>
            </AppShell.Header>
            <AppShell.Navbar p="md">
                <Link to={"/"}>Home</Link>
                <Link to={"/chart"}>Chart</Link>
                <Link to={"/highlight"}>Line Highlighting</Link>
            </AppShell.Navbar>
            <AppShell.Main>
                <Routes>
                    <Route path={"/"} exact element={<HomePage />} />
                    <Route path={"/chart"} exact element={<ChartPage/>} />
                    <Route path={"/highlight"} exact element={<HighlightPage/>} />
                </Routes>
            </AppShell.Main>
        </AppShell>
    )
};

export default PathCoverageAppShell;