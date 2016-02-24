<div class='helptitle' style='font-weight:bold;'>Help: Model service</div>
<div class='helpcontent'>
What is Model service<a href='#' class='chelp hwhat'>?</a> |
<a href="http://ambit.sourceforge.net/api_model.html" target=_blank title='Application Programming interface'>API</a>

</div>

<div id="keys" style="display:none;">
  <ul>
    <li><a href="#hdataset">Dataset</a></li>
    <li><a href="#hfeature">Variables</a></li>
    <li><a href="#hwhat"></a></li>
    <li><a href="#haction"></a></li>
    <li><a href="#hchart"></a></li>
  </ul>
  <div id="hdataset">
    Dataset or compound URI as in <a href="${ambit_root}/dataset?max=100" target=_blank>Datasets list</a>.
    Either enter/paste the URI, or search for dataset by name, by entering first few letters. The field supports autocomplete
    and will show a list of dataset names, if there is a match. The dataset names are case sensitive! 
  </div>
  <div id="hfeature">
    X, Yobs, Ypred
  </div>
  <div id="hwhat">
    Models are generated by applying machine learning algorithms to specific dataset , given specific parameters. In case of expert defined rules, models are generated by the corresponding algorithms, without requirement for a training dataset.
  </div>
  <div id="hchart">
    Scatter plots are generated if numerical data is available. Use the list boxes to select which descriptors to show.
  </div>  
  
<div id="haction">
    Once a model is built, it is assigned a model URI and can be applied to datasets and compounds.The result is a dataset, identified by a dataset URI.
    <br/>
    Model services generally expect the dataset to already contain the calculated descriptors.
    In order to calculate descriptors automatically, use the <a href="${ambit_root}/algorithm/superservice" target='superservice'>Superservice</a>
  </div>
</div>      