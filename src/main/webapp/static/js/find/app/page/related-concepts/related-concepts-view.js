define([
    'backbone',
    'jquery',
    'underscore',
    'i18n!find/nls/bundle',
    'find/app/model/documents-collection',
    'find/app/util/popover',
    'find/app/util/view-state-selector',
    'text!find/templates/app/page/related-concepts/related-concepts-view.html',
    'text!find/templates/app/page/related-concepts/related-concept-list-item.html',
    'text!find/templates/app/page/top-results-popover-contents.html',
    'text!find/templates/app/page/loading-spinner.html'
], function(Backbone, $, _, i18n, DocumentsCollection, popover, viewStateSelector, relatedConceptsView, relatedConceptListItemTemplate,
            topResultsPopoverContents, loadingSpinnerTemplate) {

    function popoverHandler($content, $target) {
        var queryText = $target.text();

        var topResultsCollection = new DocumentsCollection([], {
            indexesCollection: this.indexesCollection
        });

        topResultsCollection.fetch({
            reset: true,
            data: {
                text: queryText,
                max_results: 3,
                summary: 'context',
                index: this.queryModel.get('indexes')
            },
            success: _.bind(function() {
                var listItems = topResultsCollection.map(function(model) {
                    return this.topResultsPopoverContents({
                        title: model.get('title'),
                        summary: model.get('summary').trim().substring(0, 100) + "..."
                    });
                }, this);

                $content.html($('<ul class="list-unstyled"></ul>').append(listItems));
            }, this)
        });
    }

    return Backbone.View.extend({
        className: 'suggestions-content',

        template: _.template(relatedConceptsView),
        listItemTemplate: _.template(relatedConceptListItemTemplate),
        topResultsPopoverContents: _.template(topResultsPopoverContents),
        loadingSpinnerTemplate: _.template(loadingSpinnerTemplate)({i18n: i18n, large: false}),

        events: {
            'click .query-text' : function(e) {
                var $target = $(e.target);
                var queryText = $target.attr('data-title');
                this.queryModel.set('queryText', queryText);
            }
        },

        initialize: function(options) {
            this.queryModel = options.queryModel;
            this.entityCollection = options.entityCollection;
            this.indexesCollection = options.indexesCollection;

            // Each instance of this view gets its own bound, de-bounced popover handler
            var handlePopover = _.debounce(_.bind(popoverHandler, this), 500);

            this.listenTo(this.entityCollection, 'reset', function() {
                this.$list.empty();

                if (this.entityCollection.isEmpty()) {
                    this.selectViewState(['none']);
                } else {
                    this.selectViewState(['list']);

                    var clusters = this.entityCollection.groupBy('cluster');

                    _.each(clusters, function(entities) {
                        this.$list.append(this.listItemTemplate({entities: entities}));
                    }, this);

                    popover(this.$list.find('.query-text'), handlePopover);
                }
            });

            /*suggested links*/
            this.listenTo(this.entityCollection, 'request', function() {
                this.selectViewState(['processing']);
            });

            this.listenTo(this.entityCollection, 'error', function() {
                this.selectViewState(['error']);

                this.$error.text(i18n['search.error.relatedConcepts']);
            });
        },

        render: function() {
            this.$el.html(this.template({i18n:i18n}));

            this.$list = this.$('.related-concepts-list');
            this.$error = this.$('.related-concepts-error');

            this.$none = this.$('.related-concepts-none');

            this.$notLoading = this.$('.not-loading');

            this.$processing = this.$('.processing');
            this.$processing.append(this.loadingSpinnerTemplate);

            this.selectViewState = viewStateSelector({
                list: this.$list,
                processing: this.$processing,
                error: this.$error,
                none: this.$none,
                notLoading: this.$notLoading
            });
        }
    });

});
