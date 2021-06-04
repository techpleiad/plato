import { Component, Input, OnInit } from '@angular/core';
import { ProfileSpecTO } from '../shared/models/ProfileSpecTO';
import { ProfileAggregatorService } from '../shared/shared-services/profile-aggregator.service';
import { SprimeraFilesService } from '../shared/shared-services/sprimera-files.service';

@Component({
  selector: 'app-content-display',
  templateUrl: './content-display.component.html',
  styleUrls: ['./content-display.component.css']
})
export class ContentDisplayComponent implements OnInit {

  @Input() displayData: any;
  profileSpecTOList: ProfileSpecTO[] = [];

 

  constructor(private _sprimeraFilesService: SprimeraFilesService, private _profileAggregatorService:ProfileAggregatorService) {
    this.displayData = `Hello World`;
   }

  ngOnInit(): void {
    this._sprimeraFilesService.getFiles("custom-manager","dev","dev").subscribe((data: any[]) =>{
      console.log(data);
      for(let i=0;i<data.length;i++){
      
        this.profileSpecTOList.push(new ProfileSpecTO(
          data[i].profile,
          data[i].yaml,
          data[i].jsonNode,
        ))
      }
      //console.log(this.profileSpecTOList);
      let aggregated = this._profileAggregatorService.aggregateProfiles(this.profileSpecTOList);
      console.log(aggregated);
      this.displayData = JSON.stringify(aggregated.jsonContent,null,2);
    })
    
  }

}
