import { Injectable } from '@angular/core';
import { Observable, of } from 'rxjs';
import { ProfileSpecTO, PropertyDetail } from '../models/ProfileSpecTO';
import * as yaml from 'yaml';

@Injectable({
  providedIn: 'root'
})
export class ProfileAggregatorService {

  constructor() { }
  
  aggregateProfiles(profileSpecList: ProfileSpecTO[]): ProfileSpecTO {
    const result = yaml.parse(profileSpecList[0].yamlContent); //// Taking yaml of 1st file and convert to JSON

    const profilePropertyMapper = new Map(); ///// {key:property, value:profile}
    ////// pehli profile(highest priority wali) ki saari properties ki profile set kro.
    this.getLineOfEachPropertyValue('', result, profilePropertyMapper, false, profileSpecList[0].profile);

    ////// Baaki saari profiles ke liye bhi set kro priority wise aur merge bhi kro saath mein
    for (let i = 1; i < profileSpecList.length; ++i) {
      const profile = profileSpecList[i];
      this.mergeData('', result, yaml.parse(profile.yamlContent), profile.profile, '');
      this.getLineOfEachPropertyValue('', result, profilePropertyMapper, false, profile.profile);
    }

    const propertyList: PropertyDetail[] = [];
    profilePropertyMapper.forEach((value, key) => {
      propertyList.push(new PropertyDetail(key,value));
    });
    //console.log(propertyList);
    return new ProfileSpecTO('', '', result, propertyList);

  }

  mergeData(path: string, root: any, other: any, profile: string, parentType: string): void {
        //////// other----> yaml to json data, root----> finally merged result
    // console.log(root, other, arrayType);
    for (const key of Object.keys(other)) { ///// current file ki saari keys
      if (!root.hasOwnProperty(key)) {  ///// agar already ye key merged nhi h(root mein nhi h) to root mein daaldo
        if (parentType !== 'Array' || this.propertyType(other[key]) !== 'primitive') {
          // console.log(path, parentType, this.propertyType(other[key]), key);
          root[key] = other[key];
        }
      }
      else { ////// agar root[key] aur other[key] ka type same h(!=primitive) to futher merge kr do
        const rootType = this.propertyType(root[key]);
        if (rootType === this.propertyType(other[key]) && rootType !== 'primitive') {
          this.mergeData(`${path}.${key}`, root[key], other[key], profile, rootType);
        }
      }
    }
  }

  propertyType(value: any): string {
    if (value instanceof Array) {
      return 'Array';
    }
    else if (value instanceof Object) {
      return 'object';
    }
    return 'primitive';
  }

  generatePropertyPath(path: string, property: string): string {
    if (path === '') {
      return property;
    }
    return `${path}.${property}`;
  }

  /////// ye function har ek property ko uski profile ke saath set kr deta h
  getLineOfEachPropertyValue(path: string, root: any,  mapper: any, isArray: boolean, profile: string): void {
    for (const pro of Object.keys(root)) { //// ek ek krke saari properties lo
      const val = root[pro]; //// uss property ki value kya h
      const newPath = this.generatePropertyPath(path, pro);

      if (this.propertyType(val) === 'primitive' && isArray) {
        if (!mapper.get(path)) { //// agar pehle se profile set nhi h iss path ke liye to ab set kro.
          mapper.set(path, profile);
        }
        continue;
      }
      if (val instanceof Object) { //// agar object h to uske bhi andar jaakar sbka set kro
        this.getLineOfEachPropertyValue(newPath, val, mapper, val instanceof Array, profile);
      }
      else {
        if (!mapper.get(newPath)) {
          mapper.set(newPath, profile);
        }
      }
    }
  }
}
