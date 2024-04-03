import React, {useEffect, useState} from "react";
import {Button, Title} from "@mantine/core";
import {Chart} from "chart.js/auto";
import {Bar} from "react-chartjs-2";

const ChartPage = () => {
    const [input, setData] = useState([]);

    const getData = async () => {
        const response = await fetch("http://localhost:8080/api/paths", {
            method: "GET",
            mode: "cors",
            headers: {
                "Content-Type": "application/json",
            }
        })
            .then(response => response.json())
            .then(data => setData(data));
    }

    const getChartInput = async () => {
        console.log("BUTTON HIT");
        await getData();
    }

    // labels are just path + index
    const labels = [];
    for (let i = 0; i < input.length; i++) {
        labels.push("Path " + (i + 1).toString());
    }

    // data passed to the chart
    const data = {
        labels: labels,
        datasets: [
            {
                label: 'Paths',
                // data is the sample input
                data: input,
                backgroundColor: 'rgba(75, 192, 192, 0.2)',
                borderColor: 'rgba(75, 192, 192, 1)',
                borderWidth: 1,
            },
        ],
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
                    weight: 'bold'
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
                        size: 16
                    }
                },
            }
        }
    }

    return (
        <div>
            <Title order={2} padding={"md"}>Path Coverage - Chart</Title>
            <Button size={"compact-md"} onClick={getChartInput}>Get Data</Button>
            <Bar
                data={data}
                options={options}
            />
        </div>
    );
};

export default ChartPage;