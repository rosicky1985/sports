System.config({
  "transpiler": "traceur",
  "paths": {
    "*": "*.js",
    "github:*": "jspm_packages/github/*.js",
    "npm:*": "jspm_packages/npm/*.js"
  }
});

System.config({
  "map": {
    "bootstrap": "github:twbs/bootstrap@3.3.5",
    "bootstrap-datepicker": "github:eternicode/bootstrap-datepicker@1.3.1",
    "components/jquery": "github:components/jquery@2.1.4",
    "css": "github:systemjs/plugin-css@0.1.17",
    "d3": "npm:d3@3.5.6",
    "select2": "github:select2/select2@3.5.4",
    "sticky-table-headers": "npm:sticky-table-headers@0.1.19",
    "text": "github:systemjs/plugin-text@0.0.2",
    "traceur": "github:jmcriffey/bower-traceur@0.0.88",
    "traceur-runtime": "github:jmcriffey/bower-traceur-runtime@0.0.88",
    "github:select2/select2@3.5.4": {
      "css": "github:systemjs/plugin-css@0.1.17",
      "jquery": "github:components/jquery@2.1.4"
    },
    "github:twbs/bootstrap@3.3.5": {
      "jquery": "github:components/jquery@2.1.4"
    }
  }
});

