import React from "react";
import { Prism as SyntaxHighlighter } from 'react-syntax-highlighter';
import { solarizedlight } from 'react-syntax-highlighter/dist/esm/styles/prism';
import {useSelector} from "react-redux";
import selectors from "../State/selectors";
import {Title} from "@mantine/core";

const HighlightPage = () => {
    const codeFile = useSelector(selectors.selectCodeFile)["codeFile"];

    const notCoveredLines = [2, 3];

    const lineProps = (lineNumber) => {
        let style = {display: 'block'};
        if (notCoveredLines.includes(lineNumber)) {
            // solarized light hex is #fdf6e3
            style.backgroundColor = "#FAE7B3";
        }
        return {style};
    }

    return (
        <div>
            <Title order={2} padding={"md"}>Path Coverage - Line Highlighting</Title>
            <SyntaxHighlighter
                language="javascript"
                style={solarizedlight}
                wrapLines
                showLineNumbers
                lineProps={lineProps}
            >
                {codeFile}
            </SyntaxHighlighter>
        </div>
    );
};

export default HighlightPage;