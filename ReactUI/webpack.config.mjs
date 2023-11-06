import path, { dirname } from 'path'
import { fileURLToPath } from 'url'

import HtmlWebpackPlugin from 'html-webpack-plugin'
import CopyWebpackPlugin from 'copy-webpack-plugin'
import WebpackShellPluginNext from 'webpack-shell-plugin-next'
import Dotenv from 'dotenv-webpack';

const __filename = fileURLToPath(import.meta.url)
const __dirname = dirname(__filename)

export const entry = './src/index.tsx'

export const module = {
    rules: [
        {
            test: /\.(t|j)sx?$/,
            exclude: /(node_modules|bower_components)/,
            use: {
                loader: 'babel-loader',
                options: { presets: ['@babel/preset-env', '@babel/preset-react', '@babel/preset-typescript'] }
            }
        },
        {
            test: /\.(sa|sc|c)ss$/,
            use: ['style-loader', 'css-loader', 'sass-loader']
        },
        {
            test: /\.(png|jpe?g|gif)$/i,
            use: [{ loader: 'file-loader' }]
        },
    ],
}

export const resolve = {
    extensions: ['.js', '.jsx', '.ts', '.tsx'],
}

export const output = {
    filename: 'main.js',
    path: path.resolve(__dirname, 'dist'),
    publicPath: '/'
}

export const devServer = {
    static: path.join(__dirname, 'dist'),
    compress: true,
    port: 3005,
    historyApiFallback: true,
}

console.log("ENV: " + process.env.NODE_ENV);

export const plugins = [
    new Dotenv({
        path: `.env.${process.env.NODE_ENV}`,
    }),
    new HtmlWebpackPlugin({
        template: './public/index.html',
        favicon: './public/favicon.ico',
    }),
    new CopyWebpackPlugin({
        patterns: [{ from: 'public', to: '', globOptions: { ignore: ['**/index.html'] } }],
    }),
    new WebpackShellPluginNext({
        onBeforeBuild: {
            scripts: [],
            blocking: true,
            parallel: false,
        },
        onAfterDone: {
            scripts: [
                `node -e "(async () => { const chalk = await import('chalk'); console.log(chalk.default.green('\\n\\n\\n\\n\\nBuild Successful!')); })();"`,
                `node -e "(async () => { const chalk = await import('chalk'); console.log(chalk.default.magenta('\\n\\n\\n\\n© 2023 Nolly | Website: thenolle.com | Github: @TheNolle | Patreon: @_nolly\\n')); })();"`,
            ],
            blocking: true,
            parallel: false,
        },
    })
]

export default {
    entry,
    module,
    resolve,
    output,
    devServer,
    plugins,
}