import { Component } from '@angular/core';
import { MatDialog } from '@angular/material/dialog';
import { AddNewServiceComponent } from './add-new-service/add-new-service.component';
import { microService } from './microService';
import { DataManagerService } from './shared/shared-services/data-manager.service';

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.css']
})
export class AppComponent {
  title = 'frontend';
  services: microService[] = [];
  constructor(private _dataManagerService: DataManagerService){

  }

  ngOnInit(): void {
    this._dataManagerService.getServicesList()
        .subscribe(data => {
          this.services = JSON.parse(JSON.stringify(data));
          //console.log(data);
        });
  }
  
  reloadDisplayServices(){
    this._dataManagerService.getServicesList()
        .subscribe(data => {
          this.services = JSON.parse(JSON.stringify(data));
          //console.log(data);
        });
  }
  
}
