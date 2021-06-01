import { Component, OnInit } from '@angular/core';
import { MatDialog } from '@angular/material/dialog';
import {MatIconModule} from '@angular/material/icon';
import { GetServicesService } from '../get-services.service';
import { microService } from '../microService'
import { WorkspaceDialogueComponent } from '../workspace-dialogue/workspace-dialogue.component';


@Component({
  selector: 'app-display-services',
  templateUrl: './display-services.component.html',
  styleUrls: ['./display-services.component.css']
})
export class DisplayServicesComponent implements OnInit {

  services: microService[];
  
  constructor(private _getServices: GetServicesService, public dialog: MatDialog) { 
    this.services = [];
  }
  logServices(){
    console.log(this.services);
  }
  openWorkspace(mservice: microService){
    const dialogRef = this.dialog.open(WorkspaceDialogueComponent,{
      data: mservice,
      height: '800px',
      width: '1200px',
    });

    dialogRef.afterClosed().subscribe(result => {
      console.log(`Dialog result: ${result}`);
    });
  
  }

  ngOnInit(): void {
    this._getServices.getServicesList()
        .subscribe(data => {
          this.services = JSON.parse(JSON.stringify(data));
          console.log(data);
        });
  }

}
