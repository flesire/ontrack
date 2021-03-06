angular.module('ot.service.template', [
    'ot.service.core',
    'ot.service.form'
])
    .service('otTemplateService', function ($http, $modal, ot, otFormService, otAlertService) {
        var self = {};

        /**
         * Displays and manages the template definition for a branch.
         * @param templateDefinitionUri URL to get the template definition.
         */
        self.templateDefinition = function (templateDefinitionUri) {
            return otFormService.display({
                uri: templateDefinitionUri,
                title: "Template configuration",
                size: 'lg',
                submit: function (template) {
                    return ot.call($http.put(templateDefinitionUri, template));
                }
            });
        };

        /**
         * Synchronises the template definition with branches.
         * @param templateSyncUri URL to get the template definition.
         */
        self.templateSync = function (templateSyncUri) {
            otAlertService.displayProgressDialog({
                title: "Template synchronisation",
                promptMessage: "Synchronisation of this template is about to start. This might change the linked " +
                    "instances. Do you want to continue?",
                waitingMessage: "Synchronising the template...",
                endMessage: "Synchronisation has been done.",
                resultUri: 'app/service/service.template.syncResult.tpl.html',
                task: function () {
                    return ot.call($http.post(templateSyncUri));
                }
            });
        };

        /**
         * Gets a single branch name and creates a template instance from it.
         */
        self.createTemplateInstance = function (templateInstanceUri) {
            return otFormService.display({
                uri: templateInstanceUri,
                title: "Template instance creation",
                submit: function (data) {
                    var name = data.name;
                    var manual = data.manual;
                    delete data.name;
                    delete data.manual;
                    var request = {
                        name: name,
                        manual: manual,
                        parameters: data
                    };
                    return ot.call($http.put(templateInstanceUri, request));
                }
            });
        };

        /**
         * Disconnect a branch from its template
         */
        self.templateInstanceDisconnect = function (templateInstanceDisconnectUri) {
            return otAlertService.confirm({
                title: "Template disconnection",
                message: "Do you really want to disconnect this branch from its template? There will be no way " +
                    "to reattach it later on."
            }).then(function () {
                return ot.pageCall($http.delete(templateInstanceDisconnectUri));
            });
        };

        /**
         * Connect a branch to a template
         */
        self.templateInstanceConnect = function (templateInstanceConnectUri) {
            return otFormService.display({
                uri: templateInstanceConnectUri,
                title: "Connection to a template",
                submit: function (data) {
                    console.log('data=', data);
                    // Creation of the request
                    var request = {};
                    // Selected template
                    request.templateId = parseInt(data.connectionRequest.id, 10);
                    // Manual mode?
                    request.manual = data.connectionRequest.data.manual;
                    // Collections of parameters
                    request.parameters = {};
                    angular.forEach(data.connectionRequest.data, function (value, key) {
                        if (key != 'manual') {
                            request.parameters[key] = value;
                        }
                    });
                    // Call
                    return ot.pageCall($http.post(templateInstanceConnectUri, request));
                }
            });
        };

        /**
         * Syncs a branch against its template
         */
        self.templateInstanceSync = function (templateInstanceSyncUri) {
            return otAlertService.confirm({
                title: "Template synchronisation",
                message: "Do you really want to synchronise this branch from its template?"
            }).then(function () {
                return ot.pageCall($http.post(templateInstanceSyncUri));
            });
        };

        return self;
    })
;
