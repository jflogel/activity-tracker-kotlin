const path = require('path');

module.exports = {
  entry: './src/main/javascript/main.js',
  output: {
    filename: 'bundle.js',
    path: path.resolve(__dirname, 'src/main/resources/static/javascript')
  }
};