import React from "react";
import {Layouts} from "@tmgg/tmgg-system";
import logo from '../assets/logo.jpg'

export default class extends React.Component {


  render() {
    return <Layouts {...this.props} logo={logo}></Layouts>
  }


}
