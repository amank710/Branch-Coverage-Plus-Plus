import React from "react";
import { Prism as SyntaxHighlighter } from 'react-syntax-highlighter';
import { solarizedlight } from 'react-syntax-highlighter/dist/esm/styles/prism';
import {useSelector} from "react-redux";
import selectors from "../State/selectors";
import {Title, Text} from "@mantine/core";

const HighlightPage = () => {
    const codeFile = useSelector(selectors.selectCodeFile)["codeFile"];
    const uncovered = useSelector(selectors.selectPathCoverage)["pathCoverage"]["uncoveredPaths"];
    const notCoveredLines = [];

    // loop through all methods
    for (const method in uncovered) {
        // loop through all lines in each method's array
        for (let i = 0; i < uncovered[method].length; i++) {
            // loop through lines per path in a method's array
            for (let j = 0; j < uncovered[method][i].length; j++) {
                notCoveredLines.push(uncovered[method][i][j]);
            }
        }
    }

    const lineProps = (lineNumber) => {
        let style = {display: 'block'};
        if (notCoveredLines.includes(lineNumber)) {
            // solarized light hex is #fdf6e3
            style.backgroundColor = "#FAE7B3";
        }
        return {style};
    }

    return (
        <div className="main-container">
            <div className="title-container">
                <Title order={2} padding={"md"}>Path Coverage - Line Highlighting</Title>
            </div>
            <SyntaxHighlighter
                language="javascript"
                style={solarizedlight}
                wrapLines
                showLineNumbers
                lineProps={lineProps}
            >
                {codeFile}
            </SyntaxHighlighter>
            <div className="text-container">
                {notCoveredLines.length > 0 ?  <Text
                    c="dimmed"
                    fz="lg"
                >Highlighted lines were not reached/covered.</Text> : <></>}
            </div>
        </div>
    );
};

export default HighlightPage;