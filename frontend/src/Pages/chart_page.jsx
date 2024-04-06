import React from "react";
import { Title, Text} from "@mantine/core";
import {Chart} from "chart.js/auto";
import {Bar} from "react-chartjs-2";
import "../Styling/styles.css";
import {useSelector} from "react-redux";
import selectors from "../State/selectors";

const ChartPage = () => {
    const pathCovObject = useSelector(selectors.selectPathCoverage)["pathCoverage"];
    const pathCovScore = pathCovObject.pathCoverageScore;
    const datasets = useSelector(selectors.selectChart)["datasets"];
    const labels = useSelector(selectors.selectChart)["labels"];

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
                text: 'Path Coverage Runs',
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
                <Title order={2} padding={"md"}>Path Coverage - Chart</Title>
            </div>
            <div className="text-container">
                <Text
                    c="dimmed"
                    fz="md"
                >Your path coverage score: {pathCovScore}</Text>
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