define([
    'backbone'
], function(Backbone) {

    var Sort = {
        date: 'date',
        relevance: 'relevance'
    };

    return Backbone.Model.extend({
        defaults: {
            queryText: '',
            indexes: [],
            fieldText: null,
            minDate: null,
            maxDate: null,
            sort: Sort.relevance
        },

        getFieldTextString: function() {
            var fieldText = this.get('fieldText');

            if(fieldText) {
                return fieldText.toString();
            } else {
                return null;
            }
        },

        setParametricFieldText: function(fieldText) {
            this.set('fieldText', fieldText);
        },

        getIsoDate: function(type) {
            var date = this.get(type);
            if(date) {
                return date.toISOString();
            } else {
                return null;
            }
        }
    }, {
        Sort: Sort
    });
});