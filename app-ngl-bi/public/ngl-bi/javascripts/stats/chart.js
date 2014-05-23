 var chart1 = new Highcharts.Chart({			
	 chart : {
				renderTo : 'container1',
				zoomType : 'x',
				spacingRight : 20,
				type : 'column'
			},
			title : {
				text : 'Removing contamination : % Coli (PE)'
			},
			subtitle : {
				text : document.ontouchstart === undefined ? 'Z-score threshold : 2 / -2<br/>Materiels : AAAA to AAHK<br/>80 read\'s sets'
						: 'Z-score threshold : 2 / -2'
			},
			tooltip: {
				positioner : function() {
					return {
						x : 50,
						y : 460
					};
				},
				headerFormat : '<p style="font-size:12px; text-align:center;">{point.key}</p><table style="width:1600px;"><tr>',
				pointFormat : '<td style="color:{series.color};padding:-5;text-align:center;">{series.name}<br/><i>Z-score : </i><b>{point.y:.2f}</b></td>',
				footerFormat : '</tr></table>',
				shared : true,
				useHTML : true,
				backgroundColor : {
					linearGradient : [ 0, 0, 0, 60 ],
					stops : [ [ 0, '#667882' ], [ 1, '#FFFFFF' ] ]
				},
				borderWidth : 1,
				borderColor : '#AAA'
			},
			legend : {
				enabled : true
			},
			plotOptions: {
				column : {
					pointPadding : 0.2,
					borderWidth : 0
				},
				area : {
					fillColor: {
						linearGradient : {
							x1 : 0,
							y1 : 0,
							x2 : 0,
							y2 : 1
						},
						stops : [
								[ 0, Highcharts.getOptions().colors[0] ],
								[
										1,
										Highcharts
												.Color(
														Highcharts.getOptions().colors[0])
												.setOpacity(0).get('rgba') ] ]
					},
					lineWidth : 1,
					marker : {
						enabled : false
					},
					shadow : false,
					states : {
						hover : {
							lineWidth : 1
						}
					},
					threshold : null
				}
			},
xAxis: {
				labels : {
					enabled : true,
					rotation : -75,
					style : {
						fontSize : '10px'
					}
				},
				title : {
					text : 'Sequence\'s sets',
				},
				categories : [
						'<span style="color:#FFFFFF">BFY_AAAAOSF_1_A737Y_IND1</span>',
						'<span style="color:#FFFFFF">BFY_AAABOSF_1_A737Y_IND2</span>',
						'<span style="color:#FFFFFF">BFY_AAACOSF_1_A737Y_IND3</span>',
						'<span style="color:#FFFFFF">BFY_AAADOSF_1_A737Y_IND4</span>',
						'<span style="color:#FFFFFF">BFY_AAAEOSF_1_A737Y_IND5</span>',
						'<span style="color:#FFFFFF">BFY_AAAFOSF_1_A737Y_IND6</span>',
						'<span style="color:#FFFFFF">BFY_AAAGOSF_1_A737Y_IND7</span>',
						'<span style="color:#FFFFFF">BFY_AAAHOSF_1_A737Y_IND8</span>',
						'<span style="color:#FFFFFF">BFY_AAAIOSF_1_A737Y_IND9</span>',
						'<span style="color:#FFFFFF">BFY_AAAKOSF_1_A737Y_IND10</span>',
						'<span style="color:#FFFFFF">BFY_AABAOSF_1_A737Y_IND11</span>',
						'<span style="color:#FFFFFF">BFY_AABBOSF_1_A737Y_IND12</span>',
						'<span style="color:#FFFFFF">BFY_AABCOSF_1_A737Y_IND13</span>',
						'<span style="color:#FFFFFF">BFY_AABDOSF_1_A737Y_IND14</span>',
						'<span style="color:#FFFFFF">BFY_AABEOSF_1_A737Y_IND15</span>',
						'<span style="color:#FFFFFF">BFY_AABFOSF_1_A737Y_IND16</span>',
						'<span style="color:#FFFFFF">BFY_AABGOSF_1_A737Y_IND17</span>',
						'<span style="color:#FFFFFF">BFY_AABHOSF_1_A737Y_IND18</span>',
						'<span style="color:#FFFFFF">BFY_AABIOSF_1_A737Y_IND19</span>',
						'<span style="color:#FFFFFF">BFY_AABKOSF_1_A737Y_IND20</span>',
						'<span style="color:#FFFFFF">BFY_AACAOSF_1_A737Y_IND21</span>',
						'<span style="color:#FFFFFF">BFY_AACBOSF_1_A737Y_IND22</span>',
						'<span style="color:#FFFFFF">BFY_AACCOSF_1_A737Y_IND23</span>',
						'<span style="color:#FFFFFF">BFY_AACDOSF_1_A737Y_IND24</span>',
						'<span style="color:#FFFFFF">BFY_AACEOSF_1_A737Y_IND25</span>',
						'<span style="color:#FFFFFF">BFY_AACFOSF_1_A737Y_IND26</span>',
						'<span style="color:#FFFFFF">BFY_AACGOSF_1_A737Y_IND27</span>',
						'<span style="color:#FFFFFF">BFY_AACHOSF_1_A737Y_IND28</span>',
						'<span style="color:#FFFFFF">BFY_AACIOSF_1_A737Y_IND29</span>',
						'<span style="color:#FFFFFF">BFY_AACKOSF_1_A737Y_IND30</span>',
						'<span style="color:#FFFFFF">BFY_AADAOSF_1_A737Y_IND31</span>',
						'<span style="color:#FFFFFF">BFY_AADBOSF_1_A737Y_IND32</span>',
						'<span style="color:#FFFFFF">BFY_AADCOSF_1_A737Y_IND33</span>',
						'<span style="color:#FFFFFF">BFY_AADDOSF_1_A737Y_IND34</span>',
						'<span style="color:#FFFFFF">BFY_AADEOSF_1_A737Y_IND35</span>',
						'<b>BFY_AADFOSF_1_A737Y_IND36</b>',
						'<span style="color:#FFFFFF">BFY_AADGOSF_1_A737Y_IND37</span>',
						'<span style="color:#FFFFFF">BFY_AADHOSF_1_A737Y_IND38</span>',
						'<span style="color:#FFFFFF">BFY_AADIOSF_1_A737Y_IND39</span>',
						'<span style="color:#FFFFFF">BFY_AADKOSF_1_A737Y_IND40</span>',
						'<span style="color:#FFFFFF">BFY_AAEAOSF_1_A7D4G_IND1</span>',
						'<span style="color:#FFFFFF">BFY_AAEBOSF_1_A7D4G_IND2</span>',
						'<span style="color:#FFFFFF">BFY_AAECOSF_1_A7D4G_IND3</span>',
						'<span style="color:#FFFFFF">BFY_AAEDOSF_1_A7D4G_IND4</span>',
						'<span style="color:#FFFFFF">BFY_AAEEOSF_1_A7D4G_IND5</span>',
						'<span style="color:#FFFFFF">BFY_AAEFOSF_1_A7D4G_IND6</span>',
						'<span style="color:#FFFFFF">BFY_AAEGOSF_1_A7D4G_IND7</span>',
						'<span style="color:#FFFFFF">BFY_AAEHOSF_1_A7D4G_IND8</span>',
						'<span style="color:#FFFFFF">BFY_AAEIOSF_1_A7D4G_IND9</span>',
						'<span style="color:#FFFFFF">BFY_AAEKOSF_1_A7D4G_IND10</span>',
						'<span style="color:#FFFFFF">BFY_AAFAOSF_1_A7D4G_IND11</span>',
						'<span style="color:#FFFFFF">BFY_AAFBOSF_1_A7D4G_IND12</span>',
						'<span style="color:#FFFFFF">BFY_AAFCOSF_1_A7D4G_IND13</span>',
						'<span style="color:#FFFFFF">BFY_AAFDOSF_1_A7D4G_IND14</span>',
						'<span style="color:#FFFFFF">BFY_AAFEOSF_1_A7D4G_IND15</span>',
						'<span style="color:#FFFFFF">BFY_AAFFOSF_1_A7D4G_IND16</span>',
						'<span style="color:#FFFFFF">BFY_AAFGOSF_1_A7D4G_IND17</span>',
						'<span style="color:#FFFFFF">BFY_AAFHOSF_1_A7D4G_IND18</span>',
						'<span style="color:#FFFFFF">BFY_AAFIOSF_1_A7D4G_IND19</span>',
						'<span style="color:#FFFFFF">BFY_AAFKOSF_1_A7D4G_IND20</span>',
						'<span style="color:#FFFFFF">BFY_AAGAOSF_1_A7D4G_IND21</span>',
						'<span style="color:#FFFFFF">BFY_AAGBOSF_1_A7D4G_IND22</span>',
						'<span style="color:#FFFFFF">BFY_AAGCOSF_1_A7D4G_IND23</span>',
						'<span style="color:#FFFFFF">BFY_AAGDOSF_1_A7D4G_IND24</span>',
						'<span style="color:#FFFFFF">BFY_AAGEOSF_1_A7D4G_IND25</span>',
						'<span style="color:#FFFFFF">BFY_AAGFOSF_1_A7D4G_IND26</span>',
						'<span style="color:#FFFFFF">BFY_AAGGOSF_1_A7D4G_IND27</span>',
						'<b>BFY_AAGHOSF_1_A7D4G_IND28</b>',
						'<span style="color:#FFFFFF">BFY_AAGIOSF_1_A7D4G_IND29</span>',
						'<span style="color:#FFFFFF">BFY_AAGKOSF_1_A7D4G_IND30</span>',
						'<span style="color:#FFFFFF">BFY_AAHAOSF_1_A7D4G_IND31</span>',
						'<span style="color:#FFFFFF">BFY_AAHBOSF_1_A7D4G_IND32</span>',
						'<span style="color:#FFFFFF">BFY_AAHCOSF_1_A7D4G_IND33</span>',
						'<span style="color:#FFFFFF">BFY_AAHDOSF_1_A7D4G_IND34</span>',
						'<b>BFY_AAHEOSF_1_A7D4G_IND35</b>',
						'<span style="color:#FFFFFF">BFY_AAHFOSF_1_A7D4G_IND36</span>',
						'<span style="color:#FFFFFF">BFY_AAHGOSF_1_A7D4G_IND37</span>',
						'<span style="color:#FFFFFF">BFY_AAHHOSF_1_A7D4G_IND38</span>',
						'<span style="color:#FFFFFF">BFY_AAHIOSF_1_A7D4G_IND39</span>',
						'<span style="color:#FFFFFF">BFY_AAHKOSF_1_A7D4G_IND40</span>', ],
			},
			yAxis : {
				tickInterval : 2,
				title : {
					text : 'Z-score'
				},
				plotLines : [ {
					value : -2,
					color : 'green',
					dashStyle : 'shortdash',
					width : 2,
					label : {
						text : 'Z-score = -2'
					}
				}, {
					value : 2,
					color : 'red',
					dashStyle : 'shortdash',
					width : 2,
					label : {
						text : 'Z-score = 2'
					}
				} ]
			},
			series: [{
						name : '<b>% Coli</b><br/><i>Mean :</i> 0.50825%<br/><i>Std.Dev :</i> 0.17786%<br/><i>Z&gt;2 :</i> 0.86397%<br/><i>Z&lt;-2 :</i> 0.15253%',
						lineWidth : 2,
						data : [ 0.684524365878068, -0.6648460473518,
								-0.6648460473518, 0.122286693698956,
								0.00983915926313399, 0.459629297006423,
								-0.0463846079547772, 1.24676203805718,
								0.122286693698956, 0.178510460916868,
								-0.327503444044333, -0.215055909608511,
								1.52788087414674, 0.122286693698956,
								1.41543333971091, 0.515853064224334,
								0.909419434749713, -0.439950978480155,
								0.515853064224334, -0.833517349005534,
								-0.777293581787622, -0.6648460473518,
								-0.721069814569711, -0.327503444044333,
								0.178510460916868, -0.327503444044333,
								0.00983915926313399, 1.02186696918553,
								1.58410464136465, -0.496174745698067,
								0.234734228134779, 0.572076831442246,
								-0.6648460473518, -0.721069814569711,
								1.30298580527509, 2.20256608076167,
								-0.102608375172688, -0.0463846079547772,
								0.403405529788512, 0.00983915926313399,
								-0.215055909608511, -1.33953125396673,
								-1.170859952313, -0.6648460473518,
								-0.6648460473518, 0.00983915926313399,
								-0.215055909608511, -0.0463846079547772,
								-1.05841241787718, -0.215055909608511,
								-1.11463618509509, -0.496174745698067,
								-0.0463846079547772, 0.178510460916868,
								-0.271279676826422, 1.13431450362136,
								-0.889741116223445, -0.777293581787622,
								-0.102608375172688, -1.50820255562047,
								-1.00218865065927, -1.170859952313,
								-1.39575502118465, -1.50820255562047,
								-0.0463846079547772, -0.383727211262244,
								1.97767101189002, 4.17039793338856,
								1.75277594301838, -1.11463618509509,
								-0.327503444044333, 1.19053827083927,
								-1.45197878840256, -1.22708371953091,
								2.09011854632585, 1.02186696918553,
								-0.833517349005534, 0.122286693698956,
								0.79697190031389, -0.496174745698067, ]
					}, ],
		})
