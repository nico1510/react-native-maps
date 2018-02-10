//
//  StreetView.js
//  react-native-streetview
//
//  Created by Amit Palomo on 26/04/2017.
//  Copyright © 2017 Nester.co.il.
//

import React from 'react';
import PropTypes from 'prop-types';
import { findNodeHandle, NativeModules, Platform, requireNativeComponent, View } from 'react-native';

const propTypes = {
	...View.propTypes,

	// Center point
	coordinate: PropTypes.shape({
		latitude: PropTypes.number.isRequired,
		longitude: PropTypes.number.isRequired,
	}),

	// Allowing user gestures (panning, zooming)
	allGesturesEnabled: PropTypes.bool,

	onPositionChange: PropTypes.func,
};

class StreetView extends React.PureComponent {

	constructor(props) {
		super(props);
	}

	_onPositionChange = (event) => {
		if (this.props.onPositionChange) {
			this.props.onPositionChange(event.nativeEvent);
		}
	}

	_uiManagerCommand(name) {
		return NativeModules.UIManager['NSTStreetView'].Commands[name];
	}

	_mapManagerCommand(name) {
		return NativeModules['NSTStreetViewManager'][name];
	}

	_getHandle() {
		return findNodeHandle(this.streetView);
	}

	_runCommand(name, args) {
		switch (Platform.OS) {
			case 'android':
				NativeModules.UIManager.dispatchViewManagerCommand(
					this._getHandle(),
					this._uiManagerCommand(name),
					args
				);
				break;

			case 'ios':
				this._mapManagerCommand(name)(this._getHandle(), ...args);
				break;

			default:
				break;
		}
	}

	animateToBearing(bearing, duration) {
		this._runCommand('animateToBearing', [bearing, duration || 500]);
	}

	render() {
		console.log('HEAVY DUTY2');
		const props = {
			...this.props,
			onPositionChange: this._onPositionChange,
		}
		return <NSTStreetView ref={ref => { this.streetView = ref; }} {...props} />;
	}
}

StreetView.propTypes = propTypes;
const NSTStreetView = requireNativeComponent('NSTStreetView');
module.exports = StreetView;
