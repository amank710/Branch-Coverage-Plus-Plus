import React from "react";
import { Title, Text} from "@mantine/core";
import {Chart} from "chart.js/auto";
import {Bar} from "react-chartjs-2";
import "../Styling/styles.css";
import {useSelector} from "react-redux";
import selectors from "../State/selectors";

const ChartPage = () => {
    const pathCovObject = useSelector(selectors.selectPathCoverage)["pathCoverage"];
    const pathCovScore = pathCovObject.pathCoverageMetadata;
    const datasets = useSelector(selectors.selectChart)["datasets"];
    const labels = useSelector(selectors.selectChart)["labels"];

    const calcOverallScore = (pathCovObj) => {
        let counter = 0;
        let totalCov = 0;
        for (const method in pathCovObj) {
            // this is assuming that the tuple is (paths covered, total paths)
            totalCov += (pathCovObj[method][0] / pathCovObj[method][1]);
            counter++;
        }
        totalCov = totalCov / counter;
        return totalCov.toString() + " / 1.0";
    }


    // Assuming that the list/tuple is (# of covered paths, # of total paths)
    let pathCovScoreVal = "";
    if (typeof pathCovScore !== 'string' && !(pathCovScore instanceof String)) {
        pathCovScoreVal = calcOverallScore(pathCovScore);
    }

    // data passed to the chart
    const data = {
        labels: labels,
        datasets: datasets,
    };

    // chart options
    const options = {
        responsive: true,
        plugins: {
            legend: {
                position: 'top',
            },
            title: {
                display: true,
                text: 'Branch Coverage Runs',
                font: {
                    size: 24,
                    weight: 'normal'
                }
            },
        },
        scales: {
            y: {
                beginAtZero: true,
                title: {
                    display: true,
                    text: 'Number of Runs',
                    font: {
                        size: 14,
                        weight: 'lighter'
                    }
                },
            }
        }
    }

    return (
        <div className="main-container">
            <div className="title-container">
                <Title order={2} padding={"md"}>Branch Coverage - Chart</Title>
            </div>
            <div className="text-container">
                <Text
                    c="dimmed"
                    fz="md"
                >Your branch coverage score: {pathCovScoreVal}</Text>
            </div>
            <div className="chart-container">
                <Bar
                    data={data}
                    options={options}
                />
            </div>
        </div>
    );
};

export default ChartPage;