const HTMLWebpackPlugin = require("html-webpack-plugin");
const ReactRefreshWebpackPlugin = require("@pmmmwh/react-refresh-webpack-plugin");
const ModuleFederationPlugin = require("webpack/lib/container/ModuleFederationPlugin");

const isDevelopment = process.env.NODE_ENV !== "production";
const path = require("path");
const webpack = require("webpack");
const deps = require("./package.json").dependencies;
const pkgName = require("./package.json").name;
const appName = pkgName ? "_" + pkgName.replace(/([@\/\-])/ig, "_") : undefined;

module.exports = {
    mode: isDevelopment ? "development" : "production",
    devtool: 'inline-source-map',
    target: 'web',
    output: {
        filename: '[name].[contenthash].js',
        path: path.resolve(__dirname, "dist"),
        clean: true
    },
    devServer: {
        port: 3007,
        allowedHosts: 'all',
        historyApiFallback: true,
        hot: true,
        proxy: [
            {
                context: ['/api'],
                target: 'http://localhost:5007', //put the deployed backend url here
                pathRewrite: { '^/api': '' },
                changeOrigin: true,
                secure: false
            },
        ]
    },

    plugins: [
        new HTMLWebpackPlugin({
            template: "./public/index.html"
        }),
        isDevelopment && new webpack.HotModuleReplacementPlugin(),
        isDevelopment && new ReactRefreshWebpackPlugin(),
        new ModuleFederationPlugin({
            name: appName,
            filename: "remoteEntry.js",
            exposes: {},
            remotes: {},
            shared: {
                ...deps,
                react: {
                    singleton: true,
                    eager: true,
                    requiredVersion: deps.react,
                },
                "react-dom": {
                    singleton: true,
                    eager: true,
                    requiredVersion: deps["react-dom"],
                }
            }
        })
    ],

    resolve: {
        modules: [__dirname, "src", "node_modules"],
        extensions: ["*", ".js", ".jsx", ".tsx", ".ts"],
    },

    module: {
        rules: [
            {
                test: /\.ts$|tsx/,
                exclude: /node_modules/,
                loader: require.resolve("babel-loader"),
                options: {
                    plugins: [
                        isDevelopment && require.resolve("react-refresh/babel"),
                    ].filter(Boolean),
                },
            },
            {
                test: /\.(s([ac])ss)$/,
                use: [
                    "style-loader",
                    {
                        loader: "css-loader",
                        options: {
                            modules: true
                        }
                    },
                    "sass-loader"
                ],
            },
            {
                test: /\.(css)$/i,
                use: [
                    "style-loader",
                    "css-modules-typescript-loader",
                    {
                        loader: "css-loader",
                        options: {
                            modules: false
                        }
                    }
                ],
            },
            {
                test: /\.png|svg|jpg|gif$/,
                use: ["file-loader"],
            }
        ]
    }
}